import React from 'react';
import RouteDisplay from './components/RouteDisplay';

const Home = () => {
  return (
    <div
      style={{
        display: 'flex',
        justifyContent: 'centre',
        alignItems: 'centre',
        height: '100vh'
      }}
    >
      <RouteDisplay />
    </div>
  );
};

export default Home;