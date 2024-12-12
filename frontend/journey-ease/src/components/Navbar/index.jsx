import React from "react";
import LogIn from "../LogIn";
import { useNavigate } from "react-router-dom";
import './navbar_styles.css';

const Navbar = () => {
    const [open, setOpen] = React.useState(false);
    const navigate = useNavigate();

    const handleClickOpen = () => {
        setOpen(true);
    };

    const handleClose = () => {
        setOpen(false);
    };

    const handleLogout = () => {
        // navigate("/login"); 
        // TODO: change this to log out
        console.log("user wants to log out")
    };

    return (
        <div className="nav">
            <div className="bars"></div>
            <div className="nav-menu">
                <a href="/" className="nav-link">
                    Dashboard
                </a>
                <a href="/RouteDisplay" className="nav-link">
                    Search Route
                </a>
                <a href="/TrackFriend" className="nav-link">
                    Track Friends
                </a>
                <a href="/Annotations" className="nav-link">
                    Favorite Annotations
                </a>
            </div>
            <div className="nav-btn">
            <button className="nav-btn-link" onClick={handleClickOpen}>
                    Log In / Sign up
                </button>
                <LogIn open={open} handleClose={handleClose} />
                <button className="logout-btn-link" onClick={handleLogout}>
                    Log Out
                </button>
            </div>
        </div>
    );
};

export default Navbar;
