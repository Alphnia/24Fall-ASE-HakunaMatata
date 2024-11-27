import React from 'react';

const TrackFriend = () => {

  const API_KEY = "AIzaSyDDe643HpTH5XXUBOZLNuJrcCFBdKM4k8Q"; 
  const latitude = 40.7485302;
  const longitude = -73.9378792;
  const zoom = 17;

  // const mapSrc = `https://www.google.com/maps/embed/v1/view?key=${API_KEY}&center=${latitude},${longitude}&zoom=${zoom}`;
  const mapSrc = 'https://www.google.com/maps/embed/v1/place?key=${API_KEY}&q=${latitude},${longitude}'

  return (
    <iframe
      width="600"
      height="450"
      style={{ border: 0 }}
      loading="lazy"
      allowFullScreen
      referrerPolicy="no-referrer-when-downgrade"
      src={mapSrc}
      title="Google Map"
    ></iframe>
  );
};

export default TrackFriend;
