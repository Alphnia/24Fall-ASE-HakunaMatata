import React, { useState, useEffect, useMemo } from 'react';
import Checkbox from '@mui/material/Checkbox';
import FormControlLabel from '@mui/material/FormControlLabel';
import FormLabel from '@mui/material/FormLabel';
import Grid from '@mui/material/Grid2';
import OutlinedInput from '@mui/material/OutlinedInput';
import { styled } from '@mui/system';
import Button from '@mui/material/Button';
import ChevronRightRoundedIcon from '@mui/icons-material/ChevronRightRounded';
import { Stepper, Step, StepLabel, Typography, Box, nativeSelectClasses } from '@mui/material';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemText from '@mui/material/ListItemText';
import ChevronLeftRoundedIcon from '@mui/icons-material/ChevronLeftRounded';
import ShareLocaion from "../ShareLocation";
import { useNavigate } from 'react-router-dom';
import myData from './myData.json';

const FormGrid = styled(Grid)(() => ({
  display: 'flex',
  flexDirection: 'column',
}));

const extractStops = (obj) => {
  let stops = [];

  for (const key in obj) {
    if (typeof obj[key] === 'object' && obj[key] !== null) {
      stops = stops.concat(extractStops(obj[key]));
    }

    if (key.toLowerCase().includes('stop') && obj[key].name) {
      stops.push(obj[key].name);
    }
  }

  return stops;
};

const RouteDisplay = () => {
  const [origin, setOrigin] = useState('');
  const [destination, setDestination] = useState('');
  const [stopData, setstopData] = useState(null);
  const [message, setmessage] = useState(null);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const [stopDatajson, setTextDatajson] = useState('');

  const handleSearch = async () => {
    try {
      setmessage(null)
      const response = await fetch(`http://localhost:8080/retrieveRoute?origin=${encodeURIComponent(origin)}&destination=${encodeURIComponent(destination)}`);
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      const textData = await response.text();
      console.log(textData);
      let data;
      try {
        data = JSON.parse(textData);
        setTextDatajson(data);
      } catch (error) {
        data = textData;
      }
      if (typeof data === 'object') {
        const stops = extractStops(data);
        console.log(stops);
        setstopData(stops);
      } else{
        setmessage(data)
      }
      setError(null); // Clear any previous errors
    } catch (err) {
      setError(err.message);
      setstopData(null); // Clear previous route data
    }
  };

  const handleAnnotate = () => {
    const stopInfo = {stopData: stopData, stopDatajson: stopDatajson};
    navigate('/Annotations', {state: stopInfo});
  }
// function RouteDisplay(props) {
  return (
    <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', flexDirection: 'column', width: '100%' }}>
      <h1>Search a route:</h1>
      <Grid container spacing={3}>
        <FormGrid size={{ xs: 6 }}>
          <FormLabel htmlFor="Origin" required>
          Origin
          </FormLabel>
          <OutlinedInput
            id="Origin"
            name="Origin"
            type="text"
            placeholder="Time Square"
            autoComplete="Origin"
            required
            size="small"
            value={origin}
            onChange={(e) => setOrigin(e.target.value)}
          />
        </FormGrid>
        <FormGrid size={{ xs: 6 }}>
          <FormLabel htmlFor="state" required>
          Destination
          </FormLabel>
          <OutlinedInput
            id="Destination"
            name="Destination"
            type="text"
            placeholder="Columbia University"
            autoComplete="Destination"
            required
            size="small"
            value={destination}
            onChange={(e) => setDestination(e.target.value)}
          />
        </FormGrid>
      </Grid>
      <Button
        variant="contained"
        endIcon={<ChevronRightRoundedIcon />}
        onClick={handleSearch}
        fullWidth
        sx={{ backgroundColor: '#bee9e8',color: '#1b4965', marginTop: 2, width: '28%' }}
      >
        Search
      </Button>
      { message ? (
        <Typography variant="body1" style={{ color: '#1b4965', marginTop: '16px' }}>
          {message}
        </Typography>
      ) : (
          stopData && (
            <div>
              <Box sx={{ width: '100%', marginTop: 2 }}>
                <Typography variant="h6" style={{ fontSize: '1.5em',color: '#1b4965' }} >
                  Stop Details:
                </Typography>
                <Box sx={{ display: 'flex', alignItems: 'center', marginTop: 4 }}> 
                  <Stepper activeStep={0} alternativeLabel>
                    {stopData.slice().reverse().map((stop, index) => (
                      <Step key={index} sx={{ '& .MuiStepLabel-root': { color: '#bee9e8' } }}>
                        <StepLabel sx={{ 
                      '&.Mui-active': { color: '#009688' },
                      '&.Mui-completed': { color: '#4CAF50' } 
                    }}>{stop}</StepLabel>
                      </Step>
                    ))}
                  </Stepper>
                  <Button 
                    variant="contained" 
                    sx={{ marginLeft: 2, textTransform: 'none', backgroundColor: '#bee9e8',color: '#1b4965' }}
                    onClick={handleAnnotate}
                  >
                    Add Annotations
                  </Button>
                </Box>
              </Box>
            </div>
          )
      )}
    </div>
  );
};


export default RouteDisplay;

