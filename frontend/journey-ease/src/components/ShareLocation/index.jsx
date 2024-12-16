import React, { useState, useEffect, useMemo } from 'react';
import Button from '@mui/material/Button';
import SendIcon from '@mui/icons-material/Send';
import StopCircleIcon from '@mui/icons-material/StopCircle';

import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import TextField from '@mui/material/TextField';
import Tooltip from '@mui/material/Tooltip';
import IconButton from '@mui/material/IconButton';
import ContentCopyIcon from '@mui/icons-material/ContentCopy';

const ShareLocaion = () => {
  const [tracking, setTracking] = useState(false);
  const [response, setResponse] = useState(null);
  // const [intervalId, setIntervalId]
  const [open, setOpen] = React.useState(false);
  const [shareLink, setShareLink] = useState('');

  const handleClickOpen = () => {
    setOpen(true);
    console.log("hi")
    // Generate the share link (replace with your logic)
    const generatedLink = 'http://localhost:5173/TrackFriend/670c4dab7013573300601f64'; // Replace with actual link logic
    setShareLink(generatedLink);
  };

  const handleClose = () => {
    setOpen(false);
  };

  const handleCopy = () => {
    navigator.clipboard.writeText(shareLink).then(
      () => {
        console.log('Share link copied to clipboard!');
      },
      (err) => {
        console.error('Failed to copy link: ', err);
      }
    );
  };

  let watchId;
  let intervalId;

  const options = {
    enableHighAccuracy: true,
    timeout: 5000,
    maximumAge: 0,
  }

  function success(pos) {
    const crd = pos.coords;
    postData(crd);
    console.log("Your current position is:");
    console.log(`Latitude : ${crd.latitude}`);
    console.log(`Longitude: ${crd.longitude}`);
    console.log(`More or less ${crd.accuracy} meters.`);
  }
  
  function error(err) {
    console.warn(`ERROR(${err.code}): ${err.message}`);
  }
  
  const postData = async (crd) => {
    const data = { latitude: crd.latitude, longitude: crd.longitude }; 

    try {
      const res = await fetch('http://localhost:8080/RealTime/update_location?annoId=19', {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(data), 
      });
      if (!res.ok) {
        throw new Error(`HTTP error! status: ${res.status}`);
      }

      const result = await res; // Parse JSON response
      setResponse(result); // Save the response to state
    } catch (error) {
      console.error("Error:", error);
      setResponse({ error: error.message });
    }
  }
  
  function fetchPosition() {
    navigator.geolocation.getCurrentPosition(
      (position) => {
        if (intervalId !== null) {
          console.log("Position updated:", position.coords);
          console.log("intervalId: ", intervalId);
          const crd = position.coords;
          postData(crd);
        }
      },
      (error) => {
        console.error("Error fetching position:", error);
      },
      options
    );
  }
  
  const startShareLocation = () => {
    if (navigator.geolocation && intervalId !== null) {
      // watchId = navigator.geolocation.watchPosition(success, error, options);
      intervalId = setInterval(fetchPosition, 5000);
      console.log("Started watching position, intervalId = " + intervalId);
    } else {
      console.error("Geolocation is not supported by your browser.");
    }
  }
  
  const stopShareLocation = () => {
    if (watchId !== undefined) {
      navigator.geolocation.clearWatch(watchId);
      console.log("Stopped watching position.");
    } else if (intervalId !== null) {
      clearInterval(intervalId);
      intervalId = null; // Reset the interval ID
      console.log("Stopped sharing location.");
    } else {
      console.error("No position tracking to stop.");
    }
  }
  
  const startTracking = () => {
    startShareLocation();
    setTracking(true);
    handleClickOpen();
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
      <Dialog open={open} onClose={handleClose}>
        <DialogTitle>Copy this link and share with your friends:</DialogTitle>
        <DialogContent>
          <TextField
            label="Share Link"
            value={shareLink}
            fullWidth
            InputProps={{
              readOnly: true,
            }}
            variant="outlined"
            margin="dense"
          />
          <Tooltip title="Copy to Clipboard">
            <IconButton onClick={handleCopy} sx={{ marginTop: 1 }}>
              <ContentCopyIcon />
            </IconButton>
          </Tooltip>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose} variant="contained" color="primary">
            Close
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  );
};


export default ShareLocaion;