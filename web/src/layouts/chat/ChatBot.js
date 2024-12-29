import React, { useState, useRef, useEffect } from "react";
import PropTypes from "prop-types";
import DashboardLayout from "examples/LayoutContainers/DashboardLayout";
import DashboardNavbar from "examples/Navbars/DashboardNavbar";
import Footer from "examples/Footer";
import MDBox from "components/MDBox";
import MDTypography from "components/MDTypography";
import Card from "@mui/material/Card";
import Grid from "@mui/material/Grid";
import { getMessageFromChat } from "../../services/chatbootService";

const ChatMessage = ({ isBot, message }) => (
  <div
    style={{
      display: "flex",
      justifyContent: isBot ? "flex-start" : "flex-end",
      marginBottom: "1rem",
      padding: "0 16px",
    }}
  >
    <div
      style={{
        maxWidth: "80%",
        padding: "12px",
        borderRadius: "8px",
        backgroundColor: isBot ? "white" : "#1A73E8",
        color: isBot ? "#344767" : "white",
        boxShadow: "0 1px 2px rgba(0,0,0,0.1)",
        borderTopLeftRadius: isBot ? 0 : "8px",
        borderTopRightRadius: isBot ? "8px" : 0,
      }}
    >
      {isBot && (
        <div
          style={{
            display: "flex",
            alignItems: "center",
            marginBottom: "8px",
          }}
        >
          <div
            style={{
              width: "32px",
              height: "32px",
              backgroundColor: "#E3F2FD",
              borderRadius: "50%",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              marginRight: "8px",
            }}
          >
            🌿
          </div>
          <span style={{ fontWeight: "bold" }}>Botaniste IA</span>
        </div>
      )}
      <p style={{ margin: 0, fontSize: "14px" }}>{message}</p>
    </div>
  </div>
);

ChatMessage.propTypes = {
  isBot: PropTypes.bool.isRequired,
  message: PropTypes.string.isRequired,
};

const ChatBot = () => {
  const [message, setMessage] = useState("");
  const [messages, setMessages] = useState([
    {
      isBot: true,
      message:
        "Bienvenue dans mon jardin botanique ! Je suis là pour vous aider avec tous vos problèmes de plantes et pour vous donner des conseils sur leur entretien.",
    },
  ]);
  const [isTyping, setIsTyping] = useState(false); // Etat pour gérer l'animation "En train d'écrire"
  
  const messagesEndRef = useRef(null); // Reference to the end of the chat container

  // Scroll to the bottom whenever messages change
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // Ajoutez immédiatement le message de l'utilisateur à la conversation
    setMessages([
      ...messages,
      {
        isBot: false,
        message: message,
      },
    ]);
    setMessage(""); 
    
    // Affichez "En train d'écrire..." pendant que la réponse est attendue
    setIsTyping(true);

    try {
      const messageboot = await getMessageFromChat(message);
      // Une fois la réponse reçue, cachez "En train d'écrire..." et ajoutez la réponse du bot
      setIsTyping(false);

      setMessages([
        ...messages,
        {
          isBot: false,
          message: message,
        },
        {
          isBot: true,
          message: messageboot.response,
        },
      ]);
    } catch (error) {
      console.error("Erreur lors de la récupération du message du bot:", error);
      setIsTyping(false);
    }

    setMessage(""); // Réinitialisez le champ de saisie
  };

  return (
    <DashboardLayout>
      <DashboardNavbar />
      <MDBox pt={6} pb={3}>
        <Grid container spacing={6}>
          <Grid item xs={12}>
            <Card>
              <MDBox
                mx={2}
                mt={-3}
                py={3}
                px={2}
                variant="gradient"
                bgColor="info"
                borderRadius="lg"
                coloredShadow="info"
              >
                <MDTypography variant="h6" color="white">
                  Chat Botaniste
                </MDTypography>
              </MDBox>

              <MDBox
                style={{
                  height: "calc(100vh - 350px)",
                  display: "flex",
                  flexDirection: "column",
                }}
              >
                <MDBox
                  style={{
                    flex: 1,
                    overflowY: "auto",
                    backgroundColor: "#f8f9fa",
                    padding: "20px 0",
                  }}
                >
                  {messages.map((msg, index) => (
                    <ChatMessage key={index} isBot={msg.isBot} message={msg.message} />
                  ))}
                  {isTyping && (
                    <ChatMessage isBot={true} message="En train d'écrire..." />
                  )}
                  {/* Scroll to the end */}
                  <div ref={messagesEndRef} />
                </MDBox>

                <MDBox
                  style={{
                    padding: "20px",
                    borderTop: "1px solid #e5e7eb",
                    backgroundColor: "white",
                  }}
                >
                  <form
                    onSubmit={handleSubmit}
                    style={{
                      display: "flex",
                      gap: "12px",
                    }}
                  >
                    <input
                      type="text"
                      value={message}
                      onChange={(e) => setMessage(e.target.value)}
                      placeholder="Tapez votre message..."
                      style={{
                        flex: 1,
                        padding: "12px",
                        borderRadius: "8px",
                        border: "1px solid #e5e7eb",
                        outline: "none",
                        fontSize: "14px",
                      }}
                    />
                    <button
                      type="submit"
                      style={{
                        padding: "12px 24px",
                        backgroundColor: "#1A73E8",
                        color: "white",
                        border: "none",
                        borderRadius: "8px",
                        cursor: "pointer",
                        fontSize: "14px",
                        fontWeight: "500",
                      }}
                    >
                      Envoyer
                    </button>
                  </form>
                </MDBox>
              </MDBox>
            </Card>
          </Grid>
        </Grid>
      </MDBox>
    </DashboardLayout>
  );
};

export default ChatBot;
