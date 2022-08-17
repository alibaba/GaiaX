import typescript from 'rollup-plugin-typescript2';
import RollupCopy from 'rollup-plugin-copy'
import NodePath from 'path'
import Package from '../package.json'

const resolveFile = path => NodePath.resolve(__dirname, '..', path)

export default {
    input: [resolveFile(Package.source)],
    output: [
        {
            file: resolveFile(Package.main),
            format: 'cjs',
            sourcemap: true
        }
    ],
    plugins: [
        typescript(),
        RollupCopy({
            targets: [
                {
                    src: resolveFile('src/style'),
                    dest: resolveFile('dist')
                }
            ]
        })
    ],
    external: [
        'react',
        'react-dom',
        '@tarojs/components',
        '@tarojs/runtime',
        '@tarojs/taro',
        '@tarojs/react'
    ]
};
