import AsyncStorage from "@react-native-async-storage/async-storage";
import {
  createContext,
  ReactNode,
  useContext,
  useEffect,
  useState,
} from "react";

const STORAGE_KEY = "favourites";

type FavouritesState = {
  ids: Set<number>;
  loaded: boolean;
  isFavourite: (id: number) => boolean;
  toggle: (id: number) => void;
};

const FavouritesContext = createContext<FavouritesState | null>(null);

export function FavouritesProvider({ children }: { children: ReactNode }) {
  const [ids, setIds] = useState<Set<number>>(new Set());
  const [loaded, setLoaded] = useState(false);

  useEffect(() => {
    AsyncStorage.getItem(STORAGE_KEY)
      .then((raw) => {
        if (raw) setIds(new Set(JSON.parse(raw) as number[]));
      })
      .catch(() => {})
      .finally(() => setLoaded(true));
  }, []);

  useEffect(() => {
    if (loaded)
      AsyncStorage.setItem(STORAGE_KEY, JSON.stringify([...ids])).catch(
        () => {},
      );
  }, [ids, loaded]);

  function toggle(id: number) {
    setIds((prev) => {
      const next = new Set(prev);
      if (next.has(id)) next.delete(id);
      else next.add(id);
      return next;
    });
  }

  return (
    <FavouritesContext
      value={{ ids, loaded, isFavourite: (id) => ids.has(id), toggle }}
    >
      {children}
    </FavouritesContext>
  );
}

export function useFavourites() {
  const ctx = useContext(FavouritesContext);
  if (!ctx) throw new Error("no favourites context");
  return ctx;
}
