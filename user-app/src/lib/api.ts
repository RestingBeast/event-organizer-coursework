import { Event } from "@/types";
const EVENTS_URL =
  "https://event-organizer-coursework-default-rtdb.asia-southeast1.firebasedatabase.app/events.json";

export async function fetchEvents(): Promise<Event[]> {
    const res = await fetch(EVENTS_URL);
    if (!res.ok) throw new Error(`Received ${res.status}`);
    const data: Event[] | null = await res.json();
    return (data ?? []).sort((a, b) => a.date.localeCompare(b.date));
}
