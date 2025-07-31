/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        primary: {
          a0: '#764ba2',
          a10: '#764ba2b6',
          a20: '#764ba2cc',
          a30: '#764ba2e2',
          a40: '#764ba2',
          a50: '#cdcfed4b'
        },
        surface: {
          a0: '#121212',
          a10: '#282828',
          a20: '#3f3f3f',
          a30: '#575757',
          a40: '#717171',
          a50: '#8b8b8b'
        }
      }
    },
  },
  plugins: [],
};
