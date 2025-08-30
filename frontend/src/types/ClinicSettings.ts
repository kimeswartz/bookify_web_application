export interface ClinicSettings {
    id: string;
    clinicId: string;
    slotIntervalMinutes: number;
    openingHours: string[];
    specialOpenings: string[];
    specialClosings: string[];
}