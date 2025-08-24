import { render, screen } from '@testing-library/react';
import App from './App';

test('renders hero heading from Tailwind v4 view', () => {
    render(<App />);
    expect(
        screen.getByText(/Welcome to bookify built with Tailwind v4/i)
    ).toBeInTheDocument();
});
