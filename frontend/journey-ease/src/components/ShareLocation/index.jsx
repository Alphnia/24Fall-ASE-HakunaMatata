import React, { useState, useEffect, useMemo } from 'react';
import Button from '@mui/material/Button';
import SendIcon from '@mui/icons-material/Send';
import StopCircleIcon from '@mui/icons-material/StopCircle';
import { startShareLocation, stopShareLocation } from "./watchPosition";

const ShareLocaion = () => {
  const [tracking, setTracking] = useState(false);

  const startTracking = () => {
    startShareLocation();
    setTracking(true);
  }

  const stopTracking = () => {
    stopShareLocation();
    setTracking(false);
  }

  return (
    <div>
      <Button variant="contained" endIcon={<SendIcon />} onClick={startTracking}>
        Share Location
      </Button>
      <br />
      <Button variant="contained" endIcon={<StopCircleIcon />} onClick={stopTracking}>
        Stop Share Location
      </Button>
    </div>
  );
};


export default ShareLocaion;