import { LatLng } from 'leaflet';
import { useContext, useEffect, useState } from 'react';
import LocationContext from '../contexts/LocationContext';
import { ApiError, fetchLocation, LocationResult } from '../lib/api';

const Loader: React.FC = () => (
    <svg className="animate-spin h-5 w-5 mr-3" viewBox="0 0 24 24">
        <path
            className="opacity-75"
            fill="currentColor"
            d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
        ></path>
    </svg>
);

const Box: React.FC<{ title?: string; className?: string }> = ({
    title,
    children,
    className = '',
}) => {
    return (
        <div className={'shadow-lg p-2 rounded bg-white mb-2 ' + className}>
            {title && (
                <div className="row">
                    <span>{title}</span>
                </div>
            )}
            {children}
        </div>
    );
};

interface ContentProps {
    location: LatLng;
    locationInfo?: LocationResult;
    error?: Partial<ApiError>;
}
const Content: React.FC<ContentProps> = ({ location, locationInfo, error }) => {
    return (
        <>
            <Box title="Chosen location">
                <div className="row">
                    <span>Lat: {location.lat} </span>
                    <span>Lng: {location.lng} </span>
                </div>
            </Box>
            {error && (
                <Box
                    title="Received error"
                    className="bg-red-100 border border-red-400 text-red-700"
                >
                    <div>
                        {error.code && <span>Code: {error.code}</span>}
                        {error.message && <span>Message: {error.message}</span>}
                    </div>
                </Box>
            )}
            {locationInfo && (
                <Box title="Temperature (average)">
                    <div>{parseFloat(locationInfo.result).toFixed(2)}</div>
                </Box>
            )}
        </>
    );
};

const InfoBox: React.FC = () => {
    const { location } = useContext(LocationContext);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<Partial<ApiError>>();
    const [locationInfo, setLocationInfo] = useState<LocationResult>();

    useEffect(() => {
        if (location !== undefined) {
            setLoading(true);
            setError(undefined);
            fetchLocation(location.lat, location.lng)
                .then((data) => {
                    setLocationInfo(data);
                })
                .catch((apiError) => {
                    setError(apiError);
                })
                .finally(() => {
                    setLoading(false);
                });
        }
    }, [location]);

    return (
        <div className="flex-1 p-5 bg-gray-200">
            {loading && (
                <Box>
                    <div className="flex">
                        <Loader />
                        Loading...
                    </div>
                </Box>
            )}
            {location === undefined ? (
                <Box title="Click on a location to get started" />
            ) : (
                <Content
                    location={location}
                    locationInfo={locationInfo}
                    error={error}
                />
            )}
        </div>
    );
};

export default InfoBox;
