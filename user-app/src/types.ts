export type Session = {
  id: number;
  event_id: number;
  name: string;
  time: string;
  room?: string;
  speaker?: string;
  type?: string;
  description?: string;
};

export type Event = {
  id: number;
  name: string;
  date: string;
  location: string;
  organizer: string;
  type: string;
  status: string;
  description?: string;
  email?: string;
  phone?: string;
  sessions?: Session[];
};
