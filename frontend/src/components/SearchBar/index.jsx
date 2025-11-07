import React, { useState, useEffect, useRef } from 'react'
import { searchMovies } from '../../api/kinopoisk'
import { useNavigate } from 'react-router-dom'

export default function SearchBar() {
  const [query, setQuery] = useState('')
  const [results, setResults] = useState([])
  const [loading, setLoading] = useState(false)
  const [showDropdown, setShowDropdown] = useState(false)
  const inputRef = useRef(null)
  const dropdownRef = useRef(null)
  const navigate = useNavigate()

  useEffect(() => {
    if (query.length < 2) {
      setResults([])
      setShowDropdown(false)
      return
    }

    const delayDebounce = setTimeout(async () => {
      setLoading(true)
      try {
        const data = await searchMovies(query)
        setResults(data.results.slice(0, 5)) // limit to 5
        setShowDropdown(true)
      } catch (err) {
        console.error(err)
        setResults([])
      } finally {
        setLoading(false)
      }
    }, 300)

    return () => clearTimeout(delayDebounce)
  }, [query])

  useEffect(() => {
    function handleClickOutside(event) {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target) && inputRef.current && !inputRef.current.contains(event.target)) {
        setShowDropdown(false)
      }
    }
    document.addEventListener('mousedown', handleClickOutside)
    return () => document.removeEventListener('mousedown', handleClickOutside)
  }, [])

  function handleSelect(movie) {
    setQuery('')
    setShowDropdown(false)
    navigate('/', { state: { selectedMovieId: movie.id } })
  }

  return (
    <div className="search-bar" style={{position:'relative'}}>
      <input
        ref={inputRef}
        type="text"
        placeholder="Поиск фильмов..."
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        onFocus={() => query.length >= 2 && setShowDropdown(true)}
        style={{
          padding: '8px 12px',
          borderRadius: '8px',
          border: '1px solid rgba(255,255,255,0.1)',
          background: 'rgba(255,255,255,0.05)',
          color: 'var(--text)',
          width: '250px'
        }}
      />
      {loading && <div style={{position:'absolute', right:'10px', top:'50%', transform:'translateY(-50%)'}}>...</div>}
      {showDropdown && results.length > 0 && (
        <div
          ref={dropdownRef}
          className="search-dropdown"
          style={{
            position: 'absolute',
            top: '100%',
            left: 0,
            right: 0,
            background: 'var(--card)',
            border: '1px solid rgba(255,255,255,0.1)',
            borderRadius: '8px',
            maxHeight: '300px',
            overflowY: 'auto',
            zIndex: 10,
            marginTop: '4px'
          }}
        >
          {results.map(movie => (
            <div
              key={movie.id}
              onClick={() => handleSelect(movie)}
              style={{
                display: 'flex',
                alignItems: 'center',
                padding: '8px',
                cursor: 'pointer',
                borderBottom: '1px solid rgba(255,255,255,0.05)'
              }}
              onMouseEnter={(e) => e.target.style.background = 'rgba(255,255,255,0.05)'}
              onMouseLeave={(e) => e.target.style.background = 'transparent'}
            >
              {(movie.poster_path || movie.posterUrl) && (movie.poster_path || movie.posterUrl).startsWith('http') ? (
                <img
                  src={(movie.poster_path || movie.posterUrl)}
                  alt={movie.title || movie.nameRu}
                  style={{width: '40px', height: '60px', objectFit: 'cover', borderRadius: '4px', marginRight: '8px'}}
                />
              ) : (
                <div style={{width: '40px', height: '60px', background: '#07101a', borderRadius: '4px', marginRight: '8px'}}></div>
              )}
              <div>
                <div style={{fontSize: '14px', fontWeight: 'bold'}}>{movie.title || movie.nameRu || movie.nameOriginal}</div>
                <div style={{fontSize: '12px', color: 'var(--muted)'}}>{movie.release_date ? movie.release_date.slice(0,4) : (movie.year || '')} • ★ {movie.vote_average || ''}</div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}