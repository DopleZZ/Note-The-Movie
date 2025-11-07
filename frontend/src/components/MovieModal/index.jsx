import React, { useEffect, useState } from 'react'
import { getMovieDetails } from '../../api/kinopoisk'
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
              {(details.poster_path || details.posterUrl) && (details.poster_path || details.posterUrl).startsWith('http') ? (
                <img
                  src={(details.poster_path || details.posterUrl)}
                  alt={details.title || details.nameRu || details.nameOriginal}
                />
              ) : (
                <div className="no-poster">Нет постера</div>
              )}
            </div>

            <div className="modal-right">
              <h2 className="modal-title">{details.title || details.nameRu || details.nameOriginal} <span className="muted">({(details.release_date || details.year || '').toString().slice(0,4)})</span></h2>
              <div className="meta-row">
                <span>{(details.runtime || details.filmLength) ? (details.runtime || details.filmLength) + ' мин' : ''}</span>
                <span>★ {details.vote_average || (details.rating && details.rating.kp) || ''}</span>
                <span className="muted">{(details.genres || []).map(g => g.name || g).join(', ')}</span>
              </div>

              <p className="overview">{details.overview || details.description || details.shortDescription}</p>

              {(details.credits?.cast && details.credits.cast.length > 0) || (details.actors && details.actors.length > 0) ? (
                <div className="cast">
                  <strong>В ролях:</strong>
                  <div className="cast-list">
                    {(details.credits?.cast || details.actors).slice(0,8).map((c, idx) => (
                      <span key={c.id || idx} className="cast-item">{c.name || c}</span>
                    ))}
                  </div>
                </div>
              ) : null}

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
                {(details.kinopoiskId || details.filmId) && (
                  <a className="btn ghost" href={`https://www.kinopoisk.ru/film/${details.kinopoiskId || details.filmId}/`} target="_blank" rel="noreferrer">Kinopoisk</a>
                )}
              </div>
            </div>
          </div>
        ) : null}
      </div>
    </div>
  )
}
