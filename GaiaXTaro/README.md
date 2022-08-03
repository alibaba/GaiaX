# Lerna Getting Started Example

This repo is a small example of using Lerna 5+.

Watch this [7-minute walkthrough](https://www.youtube.com/watch?v=WgO5iG57jeQ) to see how new versions of Lerna work.

This repo contains three packages or projects:

- `header` (a library of React components)
- `footer` (a library of React components)
- `remixapp` (an app written using the Remix framework which depends on both `header` and `footer`)

```
packages/
    header/
        src/
            ...
        package.json
        rollup.config.json
        jest.config.js
    
    footer/
        src/
            ...
        package.json
        rollup.config.json
        jest.config.js
    
    remixapp/
        app/
            ...
        public/
        package.json
        remix.config.js
         
package.json
```

