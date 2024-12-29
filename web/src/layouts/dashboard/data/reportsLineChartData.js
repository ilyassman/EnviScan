// reportsLineChartData.js
export const getUserLineChartData = (userMonthData) => {
  // Ordres des mois pour le tri
  const monthOrder = [
    'JANUARY', 'FEBRUARY', 'MARCH', 'APRIL', 'MAY', 'JUNE', 
    'JULY', 'AUGUST', 'SEPTEMBER', 'OCTOBER', 'NOVEMBER', 'DECEMBER'
  ];

  // Trier et mapper les données
  const sortedData = monthOrder
    .map(month => userMonthData[month] || 0)
    // Calculer les valeurs cumulatives
    .reduce((acc, curr) => {
      const total = acc.length ? acc[acc.length - 1] + curr : curr;
      return [...acc, total];
    }, []);

  return {
    labels: monthOrder.map(month => month.substring(0, 3)),
    datasets: { 
      label: "Nouveaux utilisateurs", 
      data: sortedData 
    },
  };
};

export default {
  sales: {
    labels: ["Jan", "Fev", "Mar", "Avr", "Mai", "Juin", "Juil", "Août", "Sep", "Oct", "Nov", "Déc"],
    datasets: { label: "Nouveaux utilisateurs", data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0] },
  },
};