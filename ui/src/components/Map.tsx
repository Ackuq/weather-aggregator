import { LatLngExpression, LatLng, latLngBounds } from 'leaflet';
import 'leaflet/dist/leaflet.css';
import { useContext } from 'react';
import { MapContainer, Marker, TileLayer, useMapEvents } from 'react-leaflet';
import LocationContext from '../contexts/LocationContext';
import markerIcon from '../lib/markerIcon';

const northEastCorner = new LatLng(69.223361, 24.925865);
const southWestCorner = new LatLng(55.342048, 2.732958);

const mapBounds = latLngBounds(southWestCorner, northEastCorner);

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
        <MapContainer
            style={{ flex: 2 }}
            className="flex"
            center={INITIAL_POSITION}
            minZoom={6}
            zoom={13}
            maxZoom={13}
            maxBounds={mapBounds}
            maxBoundsViscosity={1.0}
        >
            <TileLayer
                attribution='&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
                url="https://{s}.tile.openstreetmap.fr/hot/{z}/{x}/{y}.png"
            />
            <LocationMarker />
        </MapContainer>
    );
};

export default Map;
