#!/bin/bash

npm install @blockcerts/cert-verifier-js \
            @babel/core \
            @babel/cli \
            @babel/preset-env \
            @babel/plugin-transform-optional-chaining \
            @babel/plugin-transform-nullish-coalescing-operator \
            @babel/plugin-syntax-class-properties \
            core-js@3.4 \
            babel-plugin-polyfill-corejs3 \
            babel-plugin-transform-globalthis \
            rollup \
            @rollup/plugin-babel \
            @rollup/plugin-node-resolve \
            @rollup/plugin-commonjs
npx rollup -c scripts/rollup.iife.config.mjs
git clean -df
