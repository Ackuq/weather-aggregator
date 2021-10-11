import { LatLngExpression } from 'leaflet';
import 'leaflet/dist/leaflet.css';
import { useContext } from 'react';
import { MapContainer, Marker, TileLayer, useMapEvents } from 'react-leaflet';
import LocationContext from '../contexts/LocationContext';
import markerIcon from '../lib/markerIcon';

// Lat long of Stockholm
const INITIAL_POSITION: LatLngExpression = { lat: 59.334591, lng: 18.06324 };

const LocationMarker: React.FC = () => {
    const { location, setLocation } = useContext(LocationContext);

    useMapEvents({
        click: (e) => {
            setLocation(e.latlng);
        },
    });

    return location === undefined ? null : (
        <Marker position={location} icon={markerIcon} />
    );
};

const Map: React.FC = () => {
    return (
        <MapContainer className="flex-1" center={INITIAL_POSITION} zoom={13}>
            <TileLayer
                attribution='&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
                url="https://{s}.tile.openstreetmap.fr/hot/{z}/{x}/{y}.png"
            />
            <LocationMarker />
        </MapContainer>
    );
};

export default Map;
