import React from 'react'
import { render, screen } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import App from './App'

test('renders app heading', () => {
  render(
    <MemoryRouter>
      <App />
    </MemoryRouter>
  )
  const heading = screen.getByText(/Note The Movie/i)
  expect(heading).toBeInTheDocument()
})
