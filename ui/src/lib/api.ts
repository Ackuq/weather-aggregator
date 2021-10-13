const baseURL = process.env.REACT_APP_API_URL ?? 'http://localhost:9000';

export interface LocationResult {
    result: {
        data: string;
    };
}

export interface ApiError {
    code: number;
    message: string;
}

const handleRequest = async (response: Response) => {
    const data = await response.json();
    if (!response.ok) {
        throw data;
    }
    return data;
};

export const fetchLocation = (
    lat: number,
    lng: number
): Promise<LocationResult> => {
    return fetch(`${baseURL}/${lat.toFixed(6)}/${lng.toFixed(6)}/`, {
        headers: {
            'Content-Type': 'application/json',
        },
    }).then(handleRequest);
};
