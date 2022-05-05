#!/bin/bash

npm install @blockcerts/cert-verifier-js
cp node_modules/@blockcerts/cert-verifier-js/dist/verifier-iife.js LearningMachine/app/src/main/assets/www/verifier.js
git clean -df
