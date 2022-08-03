import typescript from 'rollup-plugin-typescript2';

export default {
    input: ["src/index.tsx"],
    output: [
        {
            dir: "dist",
            entryFileNames: "[name].js",
            format: "cjs",
            exports: "named"
        }
    ],
    plugins: [
        typescript(),
    ],
    external: ["react"]
};
