// ********************************************************************************************
// * Centralized API client with CSRF & cookie handling                                       *
// *                                                                                          *
// * WHAT                                                                                     *
// * - Provides a single helper `apiFetch<T>(url, options)` for calling JSON-based APIs.      *
// * - Automatically includes cookies (`credentials: "include"`) on every request.            *
// * - Lazily fetches and caches a CSRF token from `/api/v1/auth/csrf` when needed.           *
// * - Attaches the CSRF token as `X-XSRF-TOKEN` header on all non-GET requests.              *
// * - Parses JSON responses and throws errors with useful messages when requests fail.       *
// *                                                                                          *
// * WHY                                                                                      *
// * - Centralizes networking, avoids repeating boilerplate (headers, error handling).        *
// * - Reduces the risk of forgetting CSRF protection on mutating requests.                       *
// * - Keeps the rest of the codebase cleaner and more secure by design.                      *
// ********************************************************************************************

// In-memory cache for the CSRF token (shared by all calls)
let csrfToken: string | null = null


/**
 * Fetches CSRF token from server if not already cached.
 * Uses `/api/v1/auth/csrf` endpoint which returns `{ csrfToken: string }`.
 * Store the token in-memory for reuse until page reload.
 */

async function fetchCsrfToken(): Promise<string> {
    if (!csrfToken) {
        const res = await fetch("/api/v1/auth/csrf", { credentials: "include" })
        if (!res.ok) throw new Error("Failed to fetch CSRF token")
        const data = await res.json()
        csrfToken = data.csrfToken
    }
    // Non-null assertion is safe here because we either had it cached or just set it.
    return csrfToken! // <- Non-null assertion
}

/**
 * Generic JSON fetch wrapper with cookie & CSRF handling.
 *
 * @param url     - API endpoint
 * @param options - Standard fetch options (`method`, `headers`, `body`, etc.)
 * @returns       - JSON response parsed as type T
 *
 * Behavior:
 * - Adds `Content-Type: application/json` by default.
 * - For non-GET requests, injects CSRF token header.
 * - Always includes cookies with `credentials: "include"`.
 * - Throws with a detailed error message if response not OK.
 */

export async function apiFetch<T>(
    url: string,
    options: RequestInit = {}
): Promise<T> {
    const headers: Record<string, string> = {
        "Content-Type": "application/json",
        ...(options.headers as Record<string, string>),
    }

    // Add CSRF header only for mutating requests (not GET)
    if (options.method && options.method !== "GET") {
        headers["X-XSRF-TOKEN"] = await fetchCsrfToken()
    }

    // Execute the request with cookies included
    const response = await fetch(url, {
        ...options,
        headers,
        credentials: "include",
    })

    // If the request failed, try to parse JSON problem details, else fallback to status text
    if (!response.ok) {
        const problem = await response.json().catch(() => ({}))
        throw new Error(problem.detail || response.statusText)
    }

    // Return JSON response parsed into expected type
    return response.json() as Promise<T>
}

