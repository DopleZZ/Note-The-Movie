import React, { useEffect, useState } from 'react'
import { getMovieDetails } from '../../api/tmdb'
import './MovieModal.css'

export default function MovieModal({ movieId, onClose }) {
  const [details, setDetails] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    let mounted = true
    setLoading(true)
    getMovieDetails(movieId)
      .then((data) => {
        if (!mounted) return
        setDetails(data)
        setError(null)
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
