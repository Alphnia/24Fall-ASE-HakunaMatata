import React, { useState, useEffect } from 'react';
import {
  Table,
  TableHead,
  TableRow,
  TableCell,
  TableBody,
  Button,
  Typography,
  Box,
} from '@mui/material';

const userId = localStorage.getItem('userId');

const FavoriteAnnotations = () => {
  const [annotations, setAnnotations] = useState([]);
  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() => {
    // Fetch annotations from backend
    async function fetchAnnotations() {
      try {
        const response = await fetch(`http://localhost:8080/queryAnnoByUser?userId=${userId}`);
        if (!response.ok) {
          throw new Error("Failed to fetch annotations.");
        }
        const data = await response.json();
        console.log(data);
        setAnnotations(data);
      } catch (error) {
        setErrorMessage(error.message);
      }
    }
    fetchAnnotations();
  }, [userId]);

  const handleDelete = async (routeId) => {
    try {
      const response = await fetch(`http://localhost:8080/deleteAnno?routeId=${routeId}&userId=${userId}`, {
        method: 'DELETE',
      });
      if (!response.ok) {
        throw new Error("Failed to delete annotation.");
      }
      // Remove the deleted annotation from state
      setAnnotations(annotations.filter((anno) => anno.routeId !== routeId));
    } catch (error) {
      setErrorMessage(error.message);
    }
  };

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', width: '100%' }}>
      <Typography variant="h4" sx={{ marginBottom: 8 }}>
      </Typography>

      {errorMessage && (
        <Typography variant="body1" color="error" sx={{ marginBottom: 2 }}>
          {errorMessage}
        </Typography>
      )}

      {annotations.length > 0 ? (
        <Table sx={{ width: '80%', backgroundColor: '#f5f5f5' }}>
          <TableHead>
            <TableRow>
              <TableCell>Route ID</TableCell>
              <TableCell>Stop List</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {annotations.map((anno, index) => (
              <TableRow key={index}>
                <TableCell>{anno.routeId}</TableCell>
                <TableCell>
                  {anno.stopList && anno.stopList.length > 0 ? (
                    anno.stopList.map((stop, i) => {
                      // 打印 stop 的内容，确保 stop 结构正确
                      console.log(stop);
                      return (
                        <div key={i}>
                        <Typography  variant="body2">
                          {stop.departureStop ? `Departure: ${stop.departureStop.dir}` : ''}
                        </Typography>
                        <Typography  variant="body2">
                          {stop.arrivalStop ? `Arrival: ${stop.arrivalStop.dir}` : ''}
                        </Typography>
                        </div>
                      );
                    })
                  ) : (
                    <Typography variant="body2">No stops available</Typography> 
                  )}
                </TableCell>
                <TableCell>
                  <Button
                    variant="contained"
                    color="secondary"
                    onClick={() => handleDelete(anno.routeId)}
                  >
                    Delete
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      ) : (
        <Typography variant="body1" sx={{ marginTop: 2 }}>
          No annotations found.
        </Typography>
      )}
    </Box>
  );
};

export default FavoriteAnnotations;
