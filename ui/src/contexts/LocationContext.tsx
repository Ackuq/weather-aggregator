import React from 'react';
import { LatLng } from 'leaflet';

interface ILocationContext {
    location?: LatLng;
    setLocation: React.Dispatch<React.SetStateAction<LatLng | undefined>>;
}

const LocationContext = React.createContext<ILocationContext>({
    location: undefined,
    setLocation: () => {},
});

export default LocationContext;
