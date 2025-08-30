export interface TreatmentVariant {
    id: string;
    clinicId: string;
    treatmentId: string;
    name: string;
    price: number;
    durationMinutes: number;
    bufferMinutes: number;
    active: boolean;
}