import React, { useState, useEffect } from 'react';
import ShareLocaion from "../ShareLocation";
import { useParams } from 'react-router-dom';

const TrackFriend = () => {
  const { userId } = useParams();
  const [location, setLocation] = useState(null);
  const [error, setError] = useState(null);
  const [isTracking, setIsTracking] = useState(false);
  const [intervalId, setIntervalId] = useState(null);
  // const API_KEY = "AIzaSyDDe643HpTH5XXUBOZLNuJrcCFBdKM4k8Q"; 
  // const latitude = 40.7485302;
  // const longitude = -73.9378792;
  // const zoom = 17;

  // const mapSrc = `https://www.google.com/maps/embed/v1/view?key=${API_KEY}&center=${latitude},${longitude}&zoom=${zoom}`;
  // const mapSrc = 'https://www.google.com/maps/embed/v1/place?key=${API_KEY}&q=${latitude},${longitude}'
  const fetchLocation = async () => {
    try {
      const response = await fetch(`http://localhost:8080/RealTime/trackLocation?userID=${userId}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        const errorMessage = await response.text();
        throw new Error(errorMessage || `HTTP error! Status: ${response.status}`);
      }

      const data = await response.json();
      setLocation(data);
      setError(null); // Clear any previous errors
    } catch (err) {
      setError(err.message);
      console.error('Failed to fetch location:', err);
    }
  };

  const startTracking = () => {
    if (!isTracking) {
      fetchLocation(); // Fetch immediately on start
      console.log("fetched")
      const id = setInterval(fetchLocation, 5000); // Fetch every 5 seconds
      setIntervalId(id);
      setIsTracking(true);
    }
  };

  const stopTracking = () => {
    if (intervalId) {
      clearInterval(intervalId);
      setIntervalId(null);
      setIsTracking(false);
    }
  };

  useEffect(() => {
    return () => {
      // Cleanup the interval when the component unmounts
      if (intervalId) {
        clearInterval(intervalId);
      }
    };
  }, [intervalId]);

  return (
    <div>
      <h1>Tracking User</h1>
      <p>User ID: {userId}</p>
      <button onClick={startTracking} disabled={isTracking}>
        Start Tracking
      </button>
      <button onClick={stopTracking} disabled={!isTracking}>
        Stop Tracking
      </button>
      {error && <p style={{ color: 'red' }}>Error: {error}</p>}
      {location && (
        <div>
          <h3>Location Data:</h3>
          <p>Latitude: {location.latitude}</p>
          <p>Longitude: {location.longitude}</p>
          <p>Timestamp: {location.timestamp}</p>
        </div>
      )}
    </div>
    // <iframe
    //   width="600"
    //   height="450"
    //   style={{ border: 0 }}
    //   loading="lazy"
    //   allowFullScreen
    //   referrerPolicy="no-referrer-when-downgrade"
    //   src={mapSrc}
    //   title="Google Map"
    // ></iframe>
  );
};

export default TrackFriend;
