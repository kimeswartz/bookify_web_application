export interface Booking {
    id: string;
    clinicId: string;
    staffId: string;
    roomId: string;
    treatmentVariantId: string;
    startTime: string;
    endTime: string;
    customerEmail: string;
    customerName: string;
    status: "BOOKED" | "CANCELLED" | "COMPLETED";
}