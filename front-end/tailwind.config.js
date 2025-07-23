/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        primary: {
          a0: '#8e96d6ac',
          a10: '#9ba1dba9',
          a20: '#a8acdfb6',
          a30: '#b4b8e4b2',
          a40: '#c1c3e9ac',
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
