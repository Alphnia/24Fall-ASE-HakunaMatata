import './App.css';
import React from "react";
import {
  BrowserRouter as Router,
  Routes,
  Route,
} from "react-router-dom";
import Home from "./Home";
import RouteDisplay from "./components/RouteDisplay";
import TrackFriend from "./components/TrackFriend";

function App() {
  return (
      <Router>
          <Routes>
              <Route path="/" element={<Home />} />
              <Route path="/RouteDisplay" element={<RouteDisplay />} />
              <Route path="/TrackFriend" element={<TrackFriend />} />
          </Routes>
      </Router>
  );
}

export default App;
