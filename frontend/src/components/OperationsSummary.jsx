function OperationsSummary({ flights }) {
  const disruptedCount = flights.filter((flight) => flight.status === "DISRUPTED").length;
  const recoveredCount = flights.filter((flight) => flight.status === "RECOVERED").length;
  const scheduledCount = flights.length - disruptedCount - recoveredCount;
  const cards = [
    { label: "Active incidents", value: String(disruptedCount), tone: "warning" },
    { label: "Recovered", value: String(recoveredCount), tone: "success" },
    { label: "Scheduled", value: String(scheduledCount), tone: "neutral" },
    { label: "Operations status", value: disruptedCount > 0 ? "Action needed" : "Stable", tone: "info" },
  ];

  return (
    <section className="summary-grid" aria-label="Operations summary">
      {cards.map((card) => (
        <div key={card.label} className={`summary-card ${card.tone}`}>
          <span className="summary-label">{card.label}</span>
          <strong>{card.value}</strong>
        </div>
      ))}
    </section>
  );
}

export default OperationsSummary;
