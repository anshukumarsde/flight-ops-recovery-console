import { useState } from "react";

const STATUSES = ["ORDERED", "IN_TRANSIT", "DELAYED", "DELIVERED"];

function formatStatus(status) {
  return status.replaceAll("_", " ").toLowerCase().replace(/\b\w/g, (letter) => letter.toUpperCase());
}

function PartsShipmentTracker({
  shipments,
  loading,
  updatingShipmentId,
  creatingOrder,
  onCreateOrder,
  onAddUpdate,
}) {
  const [forms, setForms] = useState({});
  const [orderForm, setOrderForm] = useState({
    partNumber: "",
    partName: "",
    quantity: 1,
    supplier: "",
    origin: "",
    destination: "",
    estimatedDelivery: "",
  });

  function formFor(shipment) {
    return forms[shipment.id] ?? {
      status: shipment.status,
      location: shipment.currentLocation,
      estimatedDelivery: shipment.estimatedDelivery,
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
        setForms((current) => ({ ...current, [shipment.id]: form }));
      }
    });
  }

  function submitOrder(event) {
    event.preventDefault();
    void onCreateOrder(orderForm).then((saved) => {
      if (saved) {
        setOrderForm({
          partNumber: "",
          partName: "",
          quantity: 1,
          supplier: "",
          origin: "",
          destination: "",
          estimatedDelivery: "",
        });
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
      <p className="shipment-intro">Order critical parts and keep their current delivery status up to date.</p>

      <form className="shipment-update-form parts-order-form" onSubmit={submitOrder}>
        <h4>Order a part</h4>
        <p className="shipment-intro">Creates a local order record for supplier follow-up; it does not send a purchase order.</p>
        <label>
          Part number
          <input required maxLength={60} value={orderForm.partNumber} onChange={(event) => setOrderForm({ ...orderForm, partNumber: event.target.value })} />
        </label>
        <label>
          Part name
          <input required maxLength={120} value={orderForm.partName} onChange={(event) => setOrderForm({ ...orderForm, partName: event.target.value })} />
        </label>
        <label>
          Quantity
          <input type="number" min="1" max="10000" required value={orderForm.quantity} onChange={(event) => setOrderForm({ ...orderForm, quantity: Number(event.target.value) })} />
        </label>
        <label>
          Supplier
          <input required maxLength={120} value={orderForm.supplier} onChange={(event) => setOrderForm({ ...orderForm, supplier: event.target.value })} />
        </label>
        <label>
          Ship from
          <input required maxLength={120} value={orderForm.origin} onChange={(event) => setOrderForm({ ...orderForm, origin: event.target.value })} />
        </label>
        <label>
          Deliver to
          <input required maxLength={120} value={orderForm.destination} onChange={(event) => setOrderForm({ ...orderForm, destination: event.target.value })} />
        </label>
        <label>
          Expected delivery
          <input type="date" required value={orderForm.estimatedDelivery} onChange={(event) => setOrderForm({ ...orderForm, estimatedDelivery: event.target.value })} />
        </label>
        <button className="primary-button shipment-submit" type="submit" disabled={creatingOrder}>
          {creatingOrder ? "Saving order…" : "Place parts order"}
        </button>
      </form>

      {loading ? (
        <p className="empty-state">Loading parts shipments…</p>
      ) : shipments.length === 0 ? (
        <p className="empty-state">No parts orders yet.</p>
      ) : (
        <div className="shipment-list">
          {shipments.map((shipment) => {
            const form = formFor(shipment);
            return (
              <article className="shipment-card" key={shipment.id}>
                <div className="shipment-heading">
                  <div>
                    <p className="eyebrow">{shipment.partNumber} · Order {shipment.trackingNumber}</p>
                    <h3>{shipment.partName}</h3>
                    <p className="shipment-supplier">{shipment.quantity} {shipment.quantity === 1 ? "unit" : "units"} · {shipment.supplier}</p>
                  </div>
                  <span className={`status-badge shipment-status ${shipment.status.toLowerCase().replaceAll("_", "-")}`}>
                    {formatStatus(shipment.status)}
                  </span>
                </div>

                <div className="shipment-route">
                  <div><span>Destination</span><strong>{shipment.destination}</strong></div>
                  <div><span>Current location</span><strong>{shipment.currentLocation}</strong></div>
                  <div><span>Expected delivery</span><strong>{new Date(`${shipment.estimatedDelivery}T12:00:00`).toLocaleDateString()}</strong></div>
                </div>

                <form className="shipment-update-form" onSubmit={(event) => submitUpdate(event, shipment)}>
                  <h4>Update current tracking</h4>
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
                    Expected delivery
                    <input type="date" required value={form.estimatedDelivery} onChange={(event) => changeForm(shipment, "estimatedDelivery", event.target.value)} />
                  </label>
                  <button className="primary-button shipment-submit" type="submit" disabled={updatingShipmentId === shipment.id}>
                    {updatingShipmentId === shipment.id ? "Saving…" : "Save tracking"}
                  </button>
                </form>
              </article>
            );
          })}
        </div>
      )}
    </section>
  );
}

export default PartsShipmentTracker;
