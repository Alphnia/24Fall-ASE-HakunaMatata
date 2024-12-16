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

const FormGrid = styled(Grid)(() => ({
  display: 'flex',
  flexDirection: 'column',
}));

function extractStops(transitDetails) {
  if (!transitDetails || !transitDetails.stopDetails || !transitDetails.transitLine) {
      throw new Error("Invalid transit details object");
  }

  return {
      arrivalStop: transitDetails.stopDetails.arrivalStop?.name || "Unknown",
      departureStop: transitDetails.stopDetails.departureStop?.name || "Unknown",
      headsign: transitDetails.headsign || "Unknown",
      stopCount: transitDetails.stopCount || 0,
      transitLine: transitDetails.transitLine || {}
  };
}


const RouteDisplay = () => {
  const [origin, setOrigin] = useState('');
  const [destination, setDestination] = useState('');
  // const [stopData, setstopData] = useState(null);
  const [message, setmessage] = useState(null);
  const [trainData, settrainData] = useState(null);
  const [error, setError] = useState(null);
  const [annoList, setAnnoList] = useState(null);
  const [routeId, setRouteId] = useState(null);
  const navigate = useNavigate();

  const [selectedFile, setSelectedFile] = useState(null); 
  const [uploadMessage, setUploadMessage] = useState("");

  const [stopDatajson, setTextDatajson] = useState('');

  const handleSearch = async () => {
    try {
      setmessage(null)
      const response = await fetch(`http://localhost:8080/retrieveRoute?origin=${encodeURIComponent(origin)}&destination=${encodeURIComponent(destination)}`);
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      const textData = await response.text();
      let data;
      let data3;
      let annoList;
      try {
        data = JSON.parse(textData)["Stoplist"];
        setAnnoList(JSON.parse(textData)["Annotatedlist"]);
        setRouteId(JSON.parse(textData)["RouteID"]);
        data3 = JSON.parse(data[0])["legs"];
        setTextDatajson(data);
      } catch (error) {
        data = textData;
      }
      if (typeof data === 'object') {
        let train = [];
        let data2 = data3[0]["steps"];
        // console.log(data2[0].transitDetails);
        for (let i = 0; i < data2.length; i++) {
          console.log(data2[i].transitDetails);
          const stops = extractStops(data2[i].transitDetails);
          console.log(stops);
          train.push(stops);
        }
        console.log(train)
        settrainData(train);
        // setstopData(stops);
      } else{
        setmessage(data)
      }
      setError(null); // Clear any previous errors
    } catch (err) {
      setError(err.message);
      // setstopData(null); // Clear previous route data
    }
  };

  const handleFileChange = (event) => {
    setSelectedFile(event.target.files[0]); // save user selected image
  };
  
  const handleFileUpload = async () => {
    if (!selectedFile) {
      alert("Please select a file to upload.");
      return;
    }
  
    const formData = new FormData();
    formData.append("image", selectedFile);
  
    try {
      const response = await fetch("https://api.imgur.com/3/image", {
        method: "POST",
        headers: {
          Authorization: "Client-ID 0ed9f6cc67b56a5",
        },
        body: formData,
      });
  
      const data = await response.json();
  
      if (response.ok) {
        const fileUrl = data.data.link; // image URL
  
        // send image url to backend endpoint
        await fetch("http://localhost:8080/insertPhotoAnno", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            routeId, // get RouteID from state
            url: fileUrl,
            userId: "sample-user-id",
            address: "sample-address",
          }),
        });
  
        setUploadMessage("File uploaded and annotation saved successfully!");
      } else {
        throw new Error(data.data.error || "Failed to upload image.");
      }
    } catch (error) {
      setUploadMessage(error.message);
    }
  };
  

  const handleAnnotate = () => {
    const stopInfo = {trainData: trainData, stopDatajson: stopDatajson, annoList: annoList, routeId: routeId};
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

      <div style={{ marginTop: "20px" }}>
        <FormLabel htmlFor="fileUpload">Upload an image:</FormLabel>
        <OutlinedInput
          id="fileUpload"
          type="file"
          inputProps={{ accept: "image/*" }} 
          onChange={handleFileChange} 
        />
        <Button
          onClick={handleFileUpload}
          sx={{ marginTop: "10px", backgroundColor: '#bee9e8', color: '#1b4965' }}
        >
          Upload Image
        </Button>
        {uploadMessage && (
          <Typography variant="body1" style={{ color: '#1b4965', marginTop: '16px' }}>
            {uploadMessage}
          </Typography>
        )}
      </div>

      { message ? (
        <Typography variant="body1" style={{ color: '#1b4965', marginTop: '16px' }}>
          {message}
        </Typography>
      ) : (
        trainData && (
            <div>
              {trainData.map((stops, trainIndex) => (
                  <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginTop: '16px' }} key={trainIndex} >
                    <Typography variant="h6" style={{ fontSize: '1.5em', color: '#1b4965' }}>
                      Train {trainIndex + 1} Stop Details:
                    </Typography>
                    <Box sx={{ display: 'flex', alignItems: 'center', marginTop: 4 }}> 
                    {stops && Object.keys(stops).length > 0 ? (
                      <Stepper activeStep={0} orientation="vertical">
                        {['arrivalStop', 'departureStop'].reverse().map((key, index) => (
                          stops[key] && (
                            <Step key={index} sx={{ '& .MuiStepLabel-root': { color: '#bee9e8' } }}>
                              <StepLabel sx={{
                                '&.Mui-active': { color: '#009688' },
                                '&.Mui-completed': { color: '#4CAF50' }
                              }}>
                                {stops[key]}
                              </StepLabel>
                            </Step>
                          )
                        ))}
                      </Stepper>
                      
                    ) : (
                      <Typography variant="body1" style={{ color: '#1b4965', marginTop: '16px' }}>
                        No stops available.
                      </Typography>
                    )}
                    
                    <Box sx={{ display: 'flex', flexDirection: 'column', marginLeft: '8px', color: '#1b4965' }}>
                      <span>{stops.headsign}</span>
                      <span>{stops.transitLine?.name}</span>
                    </Box>
                    <Button
                      variant="contained"
                      sx={{ marginLeft: 2, textTransform: 'none', backgroundColor: '#bee9e8', color: '#1b4965' }}
                      onClick={handleAnnotate}
                    >
                      Edit Annotation
                    </Button>
                  </Box>
                </Box>
              ))}
            </div>
            
          )
      )}
    </div>
  );
};


export default RouteDisplay;

