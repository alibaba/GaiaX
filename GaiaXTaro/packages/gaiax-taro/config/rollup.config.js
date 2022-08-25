import typescript from 'rollup-plugin-typescript2';
import RollupCopy from 'rollup-plugin-copy'
import RollupJson from '@rollup/plugin-json'
import RollupNodeResolve from '@rollup/plugin-node-resolve'
import RollupCommonjs from '@rollup/plugin-commonjs'
import RollupTypescript from 'rollup-plugin-typescript2'
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
        RollupNodeResolve({
            customResolveOptions: {
                moduleDirectory: 'node_modules'
            }
        }),
        RollupCommonjs({
            include: /\/node_modules\//
        }),
        RollupJson(),
        RollupTypescript(),
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
