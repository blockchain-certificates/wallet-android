import resolve from '@rollup/plugin-node-resolve';
import commonjs from '@rollup/plugin-commonjs';
import babel from '@rollup/plugin-babel';

export default {
  input: 'node_modules/@blockcerts/cert-verifier-js/dist/verifier-es/index.js',
  output: {
    file: 'LearningMachine/app/src/main/assets/www/verifier.js',
    format: 'iife',
    name: 'Verifier',
    generatedCode: 'es5',
    interop: 'auto',
    inlineDynamicImports: true
  },
  plugins: [
    resolve({
      browser: true,
      preferBuiltins: false
    }),
    commonjs({
      include: /node_modules/,
      strictRequires: true
    }),
    babel({
      babelHelpers: 'bundled',
      exclude: /node_modules\/core-js/, // allow polyfill modules
      presets: [
        ['@babel/preset-env', {
          targets: { chrome: '66' },
          modules: false,
          useBuiltIns: 'usage',
          corejs: '3.4',
          debug: true
        }]
      ],
      plugins: [
        '@babel/plugin-transform-optional-chaining',
        '@babel/plugin-transform-nullish-coalescing-operator',
        '@babel/plugin-syntax-class-properties',
        'babel-plugin-transform-globalthis'
      ]
    })
  ]
};
