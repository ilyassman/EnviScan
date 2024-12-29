/**
=========================================================
* Material Dashboard 2 React - v2.2.0
=========================================================

* Product Page: https://www.creative-tim.com/product/material-dashboard-react
* Copyright 2023 Creative Tim (https://www.creative-tim.com)

Coded by www.creative-tim.com

 =========================================================

* The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
*/

// @mui material components
import Grid from "@mui/material/Grid";

// Material Dashboard 2 React components
import MDBox from "components/MDBox";

// Material Dashboard 2 React example components
import DashboardLayout from "examples/LayoutContainers/DashboardLayout";
import DashboardNavbar from "examples/Navbars/DashboardNavbar";
import Footer from "examples/Footer";
import ReportsBarChart from "examples/Charts/BarCharts/ReportsBarChart";
import ReportsLineChart from "examples/Charts/LineCharts/ReportsLineChart";
import ComplexStatisticsCard from "examples/Cards/StatisticsCards/ComplexStatisticsCard";

// Data
import reportsBarChartData from "layouts/dashboard/data/reportsBarChartData";
import reportsLineChartData from "layouts/dashboard/data/reportsLineChartData";
import { getAllStatics } from "../../services/statisticsService"; // Import your API function
import { useEffect, useState } from "react";
import { getUserLineChartData } from "./data/reportsLineChartData";
import { getUserByMonth } from "../../services/statisticsService";


function Dashboard() {
  const [statistics, setStatistics] = useState({
    plantScan: 0,
    remindersConfigured: 0,
    chatbotInteractions: 0,
    usercount: 0,
    plantcount: 0,
  });
  const [userGrowthData, setUserGrowthData] = useState({
    labels: ["Jan", "Fev", "Mar", "Avr", "Mai", "Juin", "Juil", "Août", "Sep", "Oct", "Nov", "Déc"],
    datasets: { label: "Nouveaux utilisateurs", data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0] },
  });
  useEffect(() => {
    // Fetch the data when the component mounts
    const fetchStatistics = async () => {
      try {
        const data = await getAllStatics();
        setStatistics(data);
        const userMonthData = await getUserByMonth();
        const formattedUserGrowthData = getUserLineChartData(userMonthData);
        setUserGrowthData(formattedUserGrowthData);
      } catch (error) {
        console.error("Erreur lors de la récupération des statistiques:", error);
      }
    };

    fetchStatistics();
  }, []);

  const { sales} = reportsLineChartData;
  

  return (
    <DashboardLayout>
      <DashboardNavbar />
      <MDBox py={3}>
        <Grid container spacing={3}>
          {/* Nombre total d'utilisateurs */}
          <Grid item xs={12} md={6} lg={3}>
            <MDBox mb={1.5}>
              <ComplexStatisticsCard
                color="primary"
                icon="groups"
                title="Utilisateurs inscrits"
                count={statistics.usercount}
                percentage={{
                  color: "info",
                  amount: "",
                  label: "Total dans la base de données",
                }}
              />
            </MDBox>
          </Grid>

          {/* Nombre total de plantes dans la base de données */}
          <Grid item xs={12} md={6} lg={3}>
            <MDBox mb={1.5}>
              <ComplexStatisticsCard
                color="success"
                icon="spa"
                title="Plantes identifiées"
                count={statistics.plantcount}
                percentage={{
                  color: "info",
                  amount: "",
                  label: "Total dans la base de données",
                }}
              />
            </MDBox>
          </Grid>

          {/* Nombre de scans effectués */}
          <Grid item xs={12} md={6} lg={3}>
            <MDBox mb={1.5}>
              <ComplexStatisticsCard
                color="info"
                icon="camera_alt"
                title="Scans effectués"
                count={statistics.plantScan}
                percentage={{
                  color: "info",
                  amount: "",
                  label: "Total dans la base de données",
                }}
              />
            </MDBox>
          </Grid>

          {/* Nombre de rappels configurés */}
          <Grid item xs={12} md={6} lg={3}>
            <MDBox mb={1.5}>
              <ComplexStatisticsCard
                color="warning"
                icon="alarm"
                title="Rappels configurés"
                count={statistics.remindersConfigured}
                percentage={{
                  color: "info",
                  amount: "",
                  label: "Total dans la base de données",
                }}
              />
            </MDBox>
          </Grid>

          {/* Interactions avec le chatbot */}
          <Grid item xs={12} md={6} lg={3}>
            <MDBox mb={1.5}>
              <ComplexStatisticsCard
                color="secondary"
                icon="chat"
                title="Interactions chatbot"
                count={statistics.chatbotInteractions}
                percentage={{
                  color: "info",
                  amount: "",
                  label: "Total dans la base de données",
                }}
              />
            </MDBox>
          </Grid>
        </Grid>

        <MDBox mt={4.5}>
  <Grid container spacing={3}>
    {/* Vue des scans */}
    <Grid item xs={12} md={6}>
      <MDBox mb={3}>
        <ReportsBarChart
          color="primary"
          title="Scans effectués"
          description="Performance des scans de la semaine dernière"
          date="mis à jour il y a 2 jours"
          chart={reportsBarChartData} // Remplacez par vos données spécifiques
        />
      </MDBox>
    </Grid>
    <Grid item xs={12} md={6}>
    <MDBox mb={3}>
              <ReportsLineChart
                color="success"
                title="Croissance des utilisateurs"
                description={
                  <>
                    (<strong>+{(userGrowthData.datasets.data[userGrowthData.datasets.data.length - 1] || 0)}%</strong>) augmentation des nouveaux utilisateurs.
                  </>
                }
                date="mis à jour à l'instant"
                chart={userGrowthData}
              />
            </MDBox>
    </Grid>
  </Grid>
</MDBox>
      </MDBox>
    </DashboardLayout>
  );
}

export default Dashboard;
