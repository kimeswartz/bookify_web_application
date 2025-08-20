import { render, screen } from '@testing-library/react';
import App from './App';

test('renders hej-text från Tailwind testvy', () => {
    render(<App />);
    expect(screen.getByText(/Hej från Tailwind v3 🎉/i)).toBeInTheDocument();
});
