import L from 'leaflet';
import icon from 'leaflet/dist/images/marker-icon.png';
import iconRetina from 'leaflet/dist/images/marker-icon-2x.png';

const markerIcon = L.icon({
    iconUrl: icon,
    iconRetinaUrl: iconRetina,
    iconAnchor: [12, 41],
    popupAnchor: [1, -34],
    iconSize: [25, 41],
});

export default markerIcon;
