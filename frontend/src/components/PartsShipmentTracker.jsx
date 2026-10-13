import { useState } from "react";

const STATUSES = ["ORDERED", "IN_TRANSIT", "DELAYED", "OUT_FOR_DELIVERY", "DELIVERED"];

function formatStatus(status) {
  return status.replaceAll("_", " ").toLowerCase().replace(/\b\w/g, (letter) => letter.toUpperCase());
}

function PartsShipmentTracker({ shipments, loading, updatingShipmentId, onAddUpdate }) {
  const [forms, setForms] = useState({});

  function formFor(shipment) {
    return forms[shipment.id] ?? {
      status: shipment.status,
      location: shipment.currentLocation,
      estimatedDelivery: shipment.estimatedDelivery,
      note: "",
    };
  }

  function changeForm(shipment, field, value) {
    setForms((current) => ({
      ...current,
      [shipment.id]: { ...formFor(shipment), [field]: value },
    }));
  }

  function submitUpdate(event, shipment) {
    event.preventDefault();
    const form = formFor(shipment);
    void onAddUpdate(shipment.id, form).then((saved) => {
      if (saved) {
        setForms((current) => ({ ...current, [shipment.id]: { ...form, note: "" } }));
      }
    });
  }

  return (
    <section className="card panel page-panel">
      <div className="panel-header">
        <div>
          <p className="eyebrow">Supply chain visibility</p>
          <h2>Parts in transit</h2>
        </div>
        <span className="option-count">{shipments.length} shipments</span>
      </div>
      <p className="shipment-intro">See the latest reported location, expected delivery, and movement history for critical aircraft parts.</p>

      {loading ? (
        <p className="empty-state">Loading parts shipments…</p>
      ) : shipments.length === 0 ? (
        <p className="empty-state">No parts shipments are being tracked.</p>
      ) : (
        <div className="shipment-list">
          {shipments.map((shipment) => {
            const form = formFor(shipment);
            return (
              <article className="shipment-card" key={shipment.id}>
                <div className="shipment-heading">
                  <div>
                    <p className="eyebrow">{shipment.partNumber} · {shipment.trackingNumber}</p>
                    <h3>{shipment.partName}</h3>
                    <p className="shipment-supplier">{shipment.quantity} {shipment.quantity === 1 ? "unit" : "units"} · {shipment.supplier}</p>
                  </div>
                  <span className={`status-badge shipment-status ${shipment.status.toLowerCase().replaceAll("_", "-")}`}>
                    {formatStatus(shipment.status)}
                  </span>
                </div>

                <div className="shipment-route">
                  <div><span>Origin</span><strong>{shipment.origin}</strong></div>
                  <span className="shipment-route-arrow" aria-hidden="true">→</span>
                  <div><span>Destination</span><strong>{shipment.destination}</strong></div>
                  <div><span>Latest location</span><strong>{shipment.currentLocation}</strong></div>
                  <div><span>Estimated delivery</span><strong>{new Date(`${shipment.estimatedDelivery}T12:00:00`).toLocaleDateString()}</strong></div>
                </div>

                <div className="shipment-updates">
                  <h4>Tracking updates</h4>
                  {shipment.updates.map((update, index) => (
                    <div className="shipment-update" key={`${update.occurredAt}-${index}`}>
                      <span className="shipment-update-dot" aria-hidden="true" />
                      <div>
                        <strong>{update.location} · {formatStatus(update.status)}</strong>
                        <p>{update.note}</p>
                        <time dateTime={update.occurredAt}>{new Date(update.occurredAt).toLocaleString()}</time>
                      </div>
                    </div>
                  ))}
                </div>

                {shipment.status !== "DELIVERED" && (
                  <form className="shipment-update-form" onSubmit={(event) => submitUpdate(event, shipment)}>
                    <h4>Record tracking update</h4>
                    <label>
                      Status
                      <select value={form.status} onChange={(event) => changeForm(shipment, "status", event.target.value)}>
                        {STATUSES.map((status) => <option value={status} key={status}>{formatStatus(status)}</option>)}
                      </select>
                    </label>
                    <label>
                      Current location
                      <input required maxLength={120} value={form.location} onChange={(event) => changeForm(shipment, "location", event.target.value)} />
                    </label>
                    <label>
                      Estimated delivery
                      <input type="date" required value={form.estimatedDelivery} onChange={(event) => changeForm(shipment, "estimatedDelivery", event.target.value)} />
                    </label>
                    <label className="shipment-note-field">
                      Carrier update
                      <input required maxLength={240} placeholder="e.g. Departed regional sorting facility" value={form.note} onChange={(event) => changeForm(shipment, "note", event.target.value)} />
                    </label>
                    <button className="primary-button shipment-submit" type="submit" disabled={updatingShipmentId === shipment.id}>
                      {updatingShipmentId === shipment.id ? "Saving update…" : "Save tracking update"}
                    </button>
                  </form>
                )}
              </article>
            );
          })}
        </div>
      )}
    </section>
  );
}

export default PartsShipmentTracker;
