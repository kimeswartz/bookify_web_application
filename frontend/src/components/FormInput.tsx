import * as React from "react";

// ********************************************************************************************
// * FormInput: Reusable text input with label                                               *
// *                                                                                         *
// * WHAT                                                                                    *
// * - Wraps a standard <input> with a styled label and consistent Tailwind styling.         *
// * - Accepts all native <input> props plus a required `label` prop.                        *
// *                                                                                         *
// * WHY                                                                                     *
// * - Keeps form inputs consistent in look and behavior.                                    *
// * - Reduces boilerplate: always label + input together with the same styling.             *
// ********************************************************************************************

// Props = all standard input props (e.g. type, value, onChange) + required label
interface Props extends React.InputHTMLAttributes<HTMLInputElement>{
    label: string
}

// Functional component rendering label + input
export default function FormInput({ label, ...props } :Props) {
    return (
        <div className="mb-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">
                {label}
            </label>
            <input
                {...props}
                className="w-full rounded-lg border border-gray-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
        </div>
    )
}