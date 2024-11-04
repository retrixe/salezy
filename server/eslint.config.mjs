import love from 'eslint-config-love'
import eslintPluginPrettierRecommended from 'eslint-plugin-prettier/recommended'

export default [
  {
    ignores: ['.pnp.cjs', '.pnp.loader.mjs', '.yarn', 'dist/**/*'],
  },
  {
    ...love,
    files: ['src/**/*.js', 'src/**/*.ts'],
  },
  {
    rules: {
      // Make TypeScript ESLint less strict.
      '@typescript-eslint/no-confusing-void-expression': 'off',
      '@typescript-eslint/strict-boolean-expressions': 'off',
      '@typescript-eslint/restrict-plus-operands': 'off',
      '@typescript-eslint/no-dynamic-delete': 'off',
      '@typescript-eslint/no-var-requires': 'off',

      '@typescript-eslint/no-explicit-any': 'off',
      '@typescript-eslint/no-magic-numbers': 'off',
      '@typescript-eslint/no-unnecessary-condition': 'off',
      '@typescript-eslint/no-unsafe-argument': 'off',
      '@typescript-eslint/no-unsafe-member-access': 'off',
      '@typescript-eslint/max-params': 'off',
    },
  },
  eslintPluginPrettierRecommended,
]
