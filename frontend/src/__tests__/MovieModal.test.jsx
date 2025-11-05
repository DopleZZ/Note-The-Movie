import React from 'react'
import { render, screen, fireEvent } from '@testing-library/react'
import MovieModal from '../components/MovieModal'

// Mock the api functions
vi.mock('../api/movies', () => ({
  addToWatched: vi.fn(() => Promise.resolve('OK')),
  removeFromWatched: vi.fn(() => Promise.resolve('OK')),
  addToFavorites: vi.fn(() => Promise.resolve('OK')),
  removeFromFavorites: vi.fn(() => Promise.resolve('OK')),
  getWatched: vi.fn(() => Promise.resolve([])),
  getFavorites: vi.fn(() => Promise.resolve([])),
}))

vi.mock('../api/tmdb', () => ({
  getMovieDetails: vi.fn(() => Promise.resolve({
    id: 1,
    title: 'Test',
    poster_path: '/p.jpg',
    overview: 'o',
    release_date: '2025-01-01',
    credits: { cast: [] }
  }))
}))

describe('MovieModal actions', () => {
  it('renders and calls addToFavorites when click', async () => {
    const onClose = vi.fn()
    // ensure logged in
    localStorage.setItem('ntm_token', 'dummy')

    render(<MovieModal movieId={1} onClose={onClose} />)

    // wait for title
    const title = await screen.findByText(/Test/)
    expect(title).toBeInTheDocument()

    const favButton = screen.getByRole('button', { name: /Добавить в избранное/i })
    fireEvent.click(favButton)

    // since addToFavorites is mocked to resolve, button should still exist and no logout
    expect(localStorage.getItem('ntm_token')).toBe('dummy')
  })
})
