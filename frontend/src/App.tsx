export default function App() {
    return (
        <div className="min-h-screen flex flex-col bg-gradient-to-br from-indigo-50 to-pink-50">
            {/* Navbar */}
            <header className="w-full bg-white shadow-md">
                <div className="max-w-6xl mx-auto flex justify-between items-center px-8 py-4">
                    <h1 className="text-2xl font-bold text-indigo-600">Tailwind v4 Test</h1>
                    <nav className="space-x-6">
                        <a href="#" className="text-gray-700 hover:text-indigo-600">Home</a>
                        <a href="#" className="text-gray-700 hover:text-indigo-600">Features</a>
                        <a href="#" className="text-gray-700 hover:text-indigo-600">About</a>
                    </nav>
                </div>
            </header>

            {/* Hero */}
            <main className="flex-grow flex items-center justify-center">
                <div className="text-center px-6 max-w-2xl">
                    <h2 className="text-5xl font-extrabold text-gray-900 mb-6">
                        Welcome to bookify built with Tailwind v4
                    </h2>
                    <p className="text-lg text-gray-600 mb-8">
                        Here we have some cool classes - from colors to flexbox to grid, shadows and responsibility
                    </p>
                    <div className="flex justify-center gap-4">
                        <button className="px-6 py-3 bg-indigo-600 text-white rounded-lg shadow-lg hover:bg-indigo-700 transition">
                            Start here
                        </button>
                        <button className="px-6 py-3 bg-gray-200 text-gray-800 rounded-lg shadow-lg hover:bg-gray-300 transition">
                            read more
                        </button>
                    </div>
                </div>
            </main>

            {/* Features */}
            <section className="py-16 bg-white">
                <div className="max-w-6xl mx-auto grid grid-cols-3 gap-8 px-6">
                    <div className="bg-indigo-100 rounded-xl p-6 shadow hover:shadow-lg transition">
                        <h3 className="text-xl font-semibold text-indigo-700 mb-2">⚡ Snabbt</h3>
                        <p className="text-gray-600">Build interfaces quickly with utility-first CSS.</p>
                    </div>
                    <div className="bg-pink-100 rounded-xl p-6 shadow hover:shadow-lg transition">
                        <h3 className="text-xl font-semibold text-pink-700 mb-2">🎨 Flexibelt</h3>
                        <p className="text-gray-600">Combine classes to create your own unique designs.</p>
                    </div>
                    <div className="bg-yellow-100 rounded-xl p-6 shadow hover:shadow-lg transition">
                        <h3 className="text-xl font-semibold text-yellow-700 mb-2">🔧 Anpassningsbart</h3>
                        <p className="text-gray-600">Expand with your own colors, spacing and plugins.</p>
                    </div>
                </div>
            </section>

            {/* Footer */}
            <footer className="bg-gray-900 text-gray-400 text-center py-6 mt-auto">
                <p>© {new Date().getFullYear()} Built with ❤️ and Tailwind v4</p>
            </footer>
        </div>
    );
}
