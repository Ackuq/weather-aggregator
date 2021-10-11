import { LatLng } from 'leaflet';
import { useState } from 'react';
import './App.css';
import InfoBox from './components/InfoBox';

import Map from './components/Map';
import LocationContext from './contexts/LocationContext';

const App: React.FC = () => {
    const [location, setLocation] = useState<LatLng>();

    return (
        <LocationContext.Provider value={{ location, setLocation }}>
            <div className="flex flex-col h-screen">
                <Map />
                <InfoBox />
            </div>
        </LocationContext.Provider>
    );
};

export default App;
