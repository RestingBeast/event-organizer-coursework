import { fetchEvents } from "@/lib/api";
import type { Event } from "@/types";
import {
  createContext,
  useContext,
  useEffect,
  useState,
  type ReactNode,
} from "react";

type EventsState = {
  events: Event[];
  loading: boolean;
  error: string | null;
  reload: () => Promise<void>;
};

const EventsContext = createContext<EventsState | null>(null);

export function EventsProvider({ children }: { children: ReactNode }) {
  const [events, setEvents] = useState<Event[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  async function reload() {
    setLoading(true);
    setError(null);
    try {
      setEvents(await fetchEvents());
    } catch {
      setError("Couldn't load events. Check your connection.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    reload();
  }, []);
  return (
    <EventsContext value={{ events, loading, error, reload }}>
      {children}
    </EventsContext>
  );
}

export function useEvents() {
  const ctx = useContext(EventsContext);
  if (!ctx) throw new Error("no context");
  return ctx;
}
