import React from 'react'
import PopularMovies from './pages/PopularMovies'

export default function App() {
  return (
    <div className="app">
      <header className="header">
        <h1>Note The Movie</h1>
        <p>Составляй свои списки фильмов — пока показываем популярные</p>
      </header>

      <main>
        <PopularMovies />
      </main>

      <footer className="footer">Data provided by TMDB</footer>
    </div>
  )
}
