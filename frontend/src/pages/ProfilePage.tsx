import { useEffect, useState } from "react"
import { apiFetch } from "../api/client"

// ********************************************************************************************
// * ProfilePage: Displays logged-in user's profile info                                     *
// *                                                                                         *
// * WHAT                                                                                    *
// * - On mount: calls GET /api/v1/auth/me to fetch user profile (email + roles).            *
// * - Shows email + roles if logged in.                                                     *
// * - If request fails (e.g., not authenticated): shows "Not logged in".                    *
// *                                                                                         *
// * WHY                                                                                     *
// * - Gives user a simple overview of their account.                                        *
// * - Centralizes session check via backend instead of relying only on client state.        *
// *                                                                                         *
// * NOTE on interface:                                                                      *
// * - `MeResponse` defines expected response shape { email, roles }.                        *
// * - TypeScript uses this interface to enforce the correct structure at compile time.      *
// * - Protects against backend/frontend drift (e.g., missing field or wrong type).          *
// ********************************************************************************************

interface MeResponse {
    email: string
    roles: string[]
}

export default function ProfilePage() {
    const [me, setMe] = useState<MeResponse | null>(null)

    useEffect(() => {
        apiFetch<MeResponse>("/api/v1/auth/me")
            .then(setMe)
            .catch(() => setMe(null))
    }, [])

    return (
        <div className="max-w-md mx-auto mt-10 bg-white p-6 rounded-2xl shadow text-center">
            <h1 className="text-xl font-bold mb-4">Profile</h1>
            {me ? (
                <div>
                    <p>Email: {me.email}</p>
                    <p>Roles: {me.roles.join(", ")}</p>
                </div>
            ) : (
                <p>Not logged in</p>
            )}
        </div>
    )
}