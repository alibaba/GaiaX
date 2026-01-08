#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
华为开发者文档抓取脚本
使用 Playwright 抓取动态网页内容并保存为 Markdown 格式
"""

import asyncio
import os
import re
from pathlib import Path
from urllib.parse import urlparse
from playwright.async_api import async_playwright, Page
import html2text


class HuaweiDocScraper:
    """华为开发者文档抓取器"""

    def __init__(self, output_dir: str = "."):
        self.output_dir = Path(output_dir)
        self.output_dir.mkdir(exist_ok=True)
        self.h2t = html2text.HTML2Text()
        self.h2t.ignore_links = False
        self.h2t.ignore_images = False
        self.h2t.ignore_emphasis = False
        self.h2t.body_width = 0  # 不限制行宽
        self.h2t.unicode_snob = True
        self.h2t.skip_internal_links = False

    def sanitize_filename(self, title: str) -> str:
        """清理文件名，移除非法字符"""
        # 移除或替换非法字符
        title = re.sub(r'[<>:"/\\|?*]', '_', title)
        # 移除前后空格
        title = title.strip()
        # 限制长度
        if len(title) > 200:
            title = title[:200]
        return title

    async def wait_for_content_load(self, page: Page):
        """等待页面内容完全加载"""
        try:
            # 等待主要内容区域加载
            await page.wait_for_selector('article, .content, .doc-content, main', timeout=10000)
            # 等待网络空闲
            await page.wait_for_load_state('networkidle', timeout=15000)
            # 额外等待确保动态内容渲染
            await asyncio.sleep(2)
        except Exception as e:
            print(f"等待内容加载时出现警告: {e}")
            # 即使超时也继续，因为部分内容可能已经加载

    async def extract_content(self, page: Page) -> tuple[str, str]:
        """提取页面标题和主要内容"""
        # 获取标题
        title = await page.title()
        
        # 尝试多个可能的内容选择器
        content_selectors = [
            'article',
            '.doc-content',
            '.content',
            'main',
            '#content',
            '.markdown-body',
            '[role="main"]'
        ]
        
        content_html = None
        for selector in content_selectors:
            try:
                element = await page.query_selector(selector)
                if element:
                    content_html = await element.inner_html()
                    if content_html and len(content_html.strip()) > 100:
                        break
            except Exception:
                continue
        
        # 如果没有找到合适的内容，使用整个 body
        if not content_html:
            body = await page.query_selector('body')
            if body:
                content_html = await body.inner_html()
        
        return title, content_html or ""

    async def scrape_page(self, page: Page, url: str) -> dict:
        """抓取单个页面"""
        print(f"正在抓取: {url}")
        
        try:
            # 访问页面
            response = await page.goto(url, wait_until='domcontentloaded', timeout=30000)
            
            if not response or response.status >= 400:
                print(f"⚠️  页面加载失败: {url} (状态码: {response.status if response else 'unknown'})")
                return None
            
            # 等待内容加载
            await self.wait_for_content_load(page)
            
            # 提取内容
            title, content_html = await self.extract_content(page)
            
            # 转换为 Markdown
            markdown_content = self.h2t.handle(content_html)
            
            # 添加元数据
            metadata = f"# {title}\n\n"
            metadata += f"**源地址**: {url}\n\n"
            metadata += f"---\n\n"
            
            full_content = metadata + markdown_content
            
            return {
                'url': url,
                'title': title,
                'content': full_content
            }
            
        except Exception as e:
            print(f"❌ 抓取失败 {url}: {str(e)}")
            return None

    def save_markdown(self, data: dict) -> str:
        """保存 Markdown 文件"""
        if not data:
            return None
        
        # 从 URL 提取文件名
        url_path = urlparse(data['url']).path
        url_filename = url_path.split('/')[-1] or 'index'
        
        # 使用标题作为文件名（如果可用）
        if data['title']:
            filename = self.sanitize_filename(data['title'])
        else:
            filename = url_filename
        
        # 确保文件名唯一性
        filepath = self.output_dir / f"{filename}.md"
        counter = 1
        while filepath.exists():
            filepath = self.output_dir / f"{filename}_{counter}.md"
            counter += 1
        
        # 保存文件
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(data['content'])
        
        print(f"✅ 已保存: {filepath}")
        return str(filepath)

    async def scrape_urls(self, urls: list[str]):
        """批量抓取多个 URL"""
        async with async_playwright() as p:
            # 启动浏览器（使用 headless 模式）
            browser = await p.chromium.launch(headless=True)
            
            # 创建浏览器上下文，设置用户代理
            context = await browser.new_context(
                user_agent='Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
                viewport={'width': 1920, 'height': 1080}
            )
            
            page = await context.new_page()
            
            results = []
            for url in urls:
                try:
                    data = await self.scrape_page(page, url)
                    if data:
                        filepath = self.save_markdown(data)
                        results.append(filepath)
                    # 短暂延迟，避免请求过快
                    await asyncio.sleep(1)
                except Exception as e:
                    print(f"❌ 处理 {url} 时出错: {str(e)}")
                    continue
            
            await browser.close()
            
            return results


async def main():
    """主函数"""
    # 要抓取的 URL 列表
    urls = [
        'https://developer.huawei.com/consumer/cn/doc/harmonyos-guides/jsvm',
        'https://developer.huawei.com/consumer/cn/doc/harmonyos-guides/jsvm-introduction',
        'https://developer.huawei.com/consumer/cn/doc/harmonyos-guides/jsvm-data-types-interfaces',
        'https://developer.huawei.com/consumer/cn/doc/harmonyos-guides/use-jsvm-process',
        'https://developer.huawei.com/consumer/cn/doc/harmonyos-guides/jsvm-guidelines',
        'https://developer.huawei.com/consumer/cn/doc/harmonyos-guides/jsvm-frequently-questions',
    ]
    
    print("🚀 开始抓取华为开发者文档...\n")
    
    # 创建抓取器实例（保存到当前目录）
    scraper = HuaweiDocScraper(output_dir=".")
    
    # 执行抓取
    results = await scraper.scrape_urls(urls)
    
    print(f"\n🎉 抓取完成！共成功保存 {len(results)} 个文件")
    print(f"文件保存位置: {os.path.abspath('.')}")


if __name__ == "__main__":
    asyncio.run(main())
