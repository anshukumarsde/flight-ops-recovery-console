const TABS = ["Overview", "Flights", "Recovery", "Dispatch", "Parts", "History"];

function AppHeader({ activeTab, onTabChange }) {
  return (
    <header className="topbar">
      <div className="brand-group">
        <div className="brand-mark">FO</div>
        <div>
          <p className="brand-kicker">FlightOps</p>
          <p className="brand-title">Recovery Console</p>
        </div>
      </div>
      <nav className="topnav" aria-label="Main navigation">
        {TABS.map((tab) => (
          <button
            type="button"
            key={tab}
            className={`nav-link ${activeTab === tab ? "active" : ""}`}
            aria-current={activeTab === tab ? "page" : undefined}
            onClick={() => onTabChange(tab)}
          >
            {tab}
          </button>
        ))}
      </nav>
    </header>
  );
}

export default AppHeader;
