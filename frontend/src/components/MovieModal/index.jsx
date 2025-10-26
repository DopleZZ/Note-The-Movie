import React, { useEffect, useState } from 'react'
import { getMovieDetails } from '../../api/tmdb'
import { addToWatched, removeFromWatched, addToFavorites, removeFromFavorites, getWatched, getFavorites } from '../../api/movies'
import './MovieModal.css'

export default function MovieModal({ movieId, onClose }) {
  const [details, setDetails] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [isWatched, setIsWatched] = useState(false)
  const [isFavorite, setIsFavorite] = useState(false)
  const [actionLoading, setActionLoading] = useState(false)
  const [isLoggedIn, setIsLoggedIn] = useState(!!localStorage.getItem('ntm_token'))

  useEffect(() => {
    let mounted = true
    setLoading(true)
    getMovieDetails(movieId)
      .then((data) => {
        if (!mounted) return
        setDetails(data)
        setError(null)
        checkLists(data)
      })
      .catch((err) => {
        if (!mounted) return
        setError(err.message || 'Ошибка при загрузке данных')
      })
      .finally(() => {
        if (!mounted) return
        setLoading(false)
      })

    function onKey(e) {
      if (e.key === 'Escape') onClose()
    }

    document.addEventListener('keydown', onKey)
    return () => {
      mounted = false
      document.removeEventListener('keydown', onKey)
    }
  }, [movieId, onClose])

  async function checkLists(movie) {
    if (!isLoggedIn) return
    try {
      const [watched, favorites] = await Promise.all([getWatched(), getFavorites()])
      setIsWatched(watched.some(m => m.id === movie.id))
      setIsFavorite(favorites.some(m => m.id === movie.id))
    } catch (err) {
      // Ignore errors, user might not be logged in
    }
  }

  async function handleWatched() {
    if (!details) return
    setActionLoading(true)
    try {
      if (isWatched) {
        await removeFromWatched(details.id)
        setIsWatched(false)
      } else {
        await addToWatched(details)
        setIsWatched(true)
      }
    } catch (err) {
      alert('Ошибка: ' + err.message)
    } finally {
      setActionLoading(false)
    }
  }

  async function handleFavorite() {
    if (!details) return
    setActionLoading(true)
    try {
      if (isFavorite) {
        await removeFromFavorites(details.id)
        setIsFavorite(false)
      } else {
        await addToFavorites(details)
        setIsFavorite(true)
      }
    } catch (err) {
      alert('Ошибка: ' + err.message)
    } finally {
      setActionLoading(false)
    }
  }

  function onOverlayClick(e) {
    if (e.target === e.currentTarget) onClose()
  }

  return (
    <div className="modal-overlay" onClick={onOverlayClick} role="dialog" aria-modal="true">
      <div className="modal">
        <button className="modal-close" onClick={onClose} aria-label="Close">✕</button>

        {loading ? (
          <div className="modal-body">Загрузка...</div>
        ) : error ? (
          <div className="modal-body error">Ошибка: {error}</div>
        ) : details ? (
          <div className="modal-body">
            <div className="modal-left">
              {details.poster_path ? (
                <img src={`https://image.tmdb.org/t/p/w342${details.poster_path}`} alt={details.title} />
              ) : (
                <div className="no-poster">Нет постера</div>
              )}
            </div>

            <div className="modal-right">
              <h2 className="modal-title">{details.title} <span className="muted">({details.release_date?.slice(0,4)})</span></h2>
              <div className="meta-row">
                <span>{details.runtime ? details.runtime + ' мин' : ''}</span>
                <span>★ {details.vote_average}</span>
                <span className="muted">{details.genres?.map(g => g.name).join(', ')}</span>
              </div>

              <p className="overview">{details.overview}</p>

              {details.credits?.cast && details.credits.cast.length > 0 && (
                <div className="cast">
                  <strong>В ролях:</strong>
                  <div className="cast-list">
                    {details.credits.cast.slice(0,8).map(c => (
                      <span key={c.id} className="cast-item">{c.name}</span>
                    ))}
                  </div>
                </div>
              )}

              <div className="actions">
                {isLoggedIn ? (
                  <>
                    <button className={`btn ${isWatched ? 'secondary' : ''}`} onClick={handleWatched} disabled={actionLoading}>
                      {isWatched ? 'Убрать из просмотренных' : 'Отметить просмотренным'}
                    </button>
                    <button className={`btn ${isFavorite ? 'secondary' : ''}`} onClick={handleFavorite} disabled={actionLoading}>
                      {isFavorite ? 'Убрать из избранных' : 'Добавить в избранное'}
                    </button>
                  </>
                ) : (
                  <p style={{color: 'var(--muted)', fontSize: '14px'}}>Войдите в аккаунт, чтобы добавлять фильмы в списки</p>
                )}
                {details.homepage && (
                  <a className="btn" href={details.homepage} target="_blank" rel="noreferrer">Официальный сайт</a>
                )}
                <a className="btn ghost" href={`https://www.themoviedb.org/movie/${details.id}`} target="_blank" rel="noreferrer">TMDB</a>
              </div>
            </div>
          </div>
        ) : null}
      </div>
    </div>
  )
}
