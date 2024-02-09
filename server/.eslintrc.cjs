module.exports = {
  root: true,
  extends: ['standard-with-typescript', 'plugin:prettier/recommended'],
  plugins: ['@typescript-eslint'],
  parser: '@typescript-eslint/parser',
  parserOptions: { project: './tsconfig.json' },
  overrides: [{ files: ['*.ts', '*.tsx'] }],
  ignorePatterns: ['.eslintrc.cjs', 'dist'],
  rules: {
    // Make TypeScript ESLint less strict.
    '@typescript-eslint/no-confusing-void-expression': 'off',
    '@typescript-eslint/strict-boolean-expressions': 'off',
    '@typescript-eslint/restrict-plus-operands': 'off',
    '@typescript-eslint/no-dynamic-delete': 'off',
    '@typescript-eslint/no-var-requires': 'off',
  },
}
