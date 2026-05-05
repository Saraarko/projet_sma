"use client";

import { useEffect, useState } from "react";

const API_URL = "http://localhost:8080/api/data";
const POLL_INTERVAL = 2000;

type Incident = {
  id: string;
  type: string;
  severity: number;
  locationX: number;
  locationY: number;
  reportedAt: string;
  assignedUnit: string;
  status: "PENDING" | "ASSIGNED" | "COMPLETED" | "ABORTED";
  responseTimeSeconds?: number;
};

type AgentStatus = {
  name: string;
  type: string;
  status: string;
  x: number;
  y: number;
};

type Stats = {
  total: number;
  completed: number;
  assigned: number;
  pending: number;
  aborted: number;
  avgResponseTimeSeconds: number;
};

type ApiData = {
  incidents: Incident[];
  statistics: Stats;
  agents: AgentStatus[];
  recentEvents: string[];
};

const STATUS_COLORS: Record<string, string> = {
  PENDING: "bg-yellow-100 text-yellow-800",
  ASSIGNED: "bg-blue-100 text-blue-800",
  COMPLETED: "bg-green-100 text-green-800",
  ABORTED: "bg-red-100 text-red-800",
};

const AGENT_COLORS: Record<string, string> = {
  Sensor: "#6366f1",
  Ambulance: "#ef4444",
  FireTruck: "#f97316",
  Police: "#3b82f6",
  Biohazard: "#84cc16",
  Dispatcher: "#8b5cf6",
  MedicalCoordinator: "#ec4899",
  TrafficController: "#14b8a6",
};

const AGENT_STATUS_DOT: Record<string, string> = {
  IDLE: "bg-green-400",
  RESPONDING: "bg-yellow-400",
  ON_SCENE: "bg-red-400",
};

const INCIDENT_COLORS: Record<string, string> = {
  FIRE: "#f97316",
  MEDICAL: "#ef4444",
  STRUCTURAL_COLLAPSE: "#78716c",
  BIOHAZARD: "#84cc16",
  CRYOGENIC_LEAK: "#38bdf8",
};

export default function Dashboard() {
  const [data, setData] = useState<ApiData | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [lastUpdate, setLastUpdate] = useState<Date | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const res = await fetch(API_URL);
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        const json = await res.json();
        setData(json);
        setLastUpdate(new Date());
        setError(null);
      } catch (e: unknown) {
        setError(e instanceof Error ? e.message : "Connection failed");
      }
    };

    fetchData();
    const interval = setInterval(fetchData, POLL_INTERVAL);
    return () => clearInterval(interval);
  }, []);

  const stats = data?.statistics;
  const incidents = data?.incidents ?? [];
  const agents = data?.agents ?? [];
  const events = data?.recentEvents ?? [];

  return (
    <div className="min-h-screen bg-gray-950 text-gray-100 p-4 font-mono">
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-white">SRUU Emergency Response Dashboard</h1>
          <p className="text-gray-400 text-sm mt-1">Multi-Agent System — Real-time Monitor</p>
        </div>
        <div className="text-right text-xs text-gray-500">
          {error ? (
            <span className="text-red-400">⚠ {error} — waiting for agents...</span>
          ) : lastUpdate ? (
            <span className="text-green-400">● Live · {lastUpdate.toLocaleTimeString()}</span>
          ) : (
            <span className="text-gray-500">Connecting...</span>
          )}
        </div>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-2 md:grid-cols-5 gap-3 mb-6">
        {[
          { label: "Total Incidents", value: stats?.total ?? 0, color: "text-white" },
          { label: "Pending", value: stats?.pending ?? 0, color: "text-yellow-400" },
          { label: "Assigned", value: stats?.assigned ?? 0, color: "text-blue-400" },
          { label: "Completed", value: stats?.completed ?? 0, color: "text-green-400" },
          { label: "Avg Response", value: stats ? `${stats.avgResponseTimeSeconds.toFixed(1)}s` : "—", color: "text-purple-400" },
        ].map((s) => (
          <div key={s.label} className="bg-gray-900 border border-gray-700 rounded-lg p-4">
            <div className={`text-2xl font-bold ${s.color}`}>{s.value}</div>
            <div className="text-gray-400 text-xs mt-1">{s.label}</div>
          </div>
        ))}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4 mb-4">
        {/* Map */}
        <div className="lg:col-span-2 bg-gray-900 border border-gray-700 rounded-lg p-4">
          <h2 className="text-sm font-semibold text-gray-300 mb-3">Deployment Map (100×100 grid)</h2>
          <div className="relative bg-gray-800 rounded" style={{ paddingBottom: "60%", overflow: "hidden" }}>
            <svg
              viewBox="0 0 100 100"
              className="absolute inset-0 w-full h-full"
              style={{ background: "#1e293b" }}
            >
              {/* Grid lines */}
              {[20, 40, 60, 80].map((v) => (
                <g key={v}>
                  <line x1={v} y1={0} x2={v} y2={100} stroke="#334155" strokeWidth={0.3} />
                  <line x1={0} y1={v} x2={100} y2={v} stroke="#334155" strokeWidth={0.3} />
                </g>
              ))}

              {/* Active incidents */}
              {incidents
                .filter((i) => i.status !== "COMPLETED" && i.status !== "ABORTED")
                .map((inc) => (
                  <g key={inc.id}>
                    <circle
                      cx={inc.locationX}
                      cy={inc.locationY}
                      r={inc.severity * 0.6 + 1}
                      fill={INCIDENT_COLORS[inc.type] ?? "#fff"}
                      fillOpacity={0.25}
                      stroke={INCIDENT_COLORS[inc.type] ?? "#fff"}
                      strokeWidth={0.5}
                    />
                    <text x={inc.locationX} y={inc.locationY - 3} fontSize={2.5} fill="#fff" textAnchor="middle">
                      {inc.type.slice(0, 3)}
                    </text>
                  </g>
                ))}

              {/* Agents */}
              {agents.map((agent) => (
                <g key={agent.name}>
                  <circle
                    cx={agent.x}
                    cy={agent.y}
                    r={3}
                    fill={AGENT_COLORS[agent.type] ?? "#999"}
                    opacity={0.9}
                  />
                  <text x={agent.x} y={agent.y + 6} fontSize={2.5} fill="#cbd5e1" textAnchor="middle">
                    {agent.name}
                  </text>
                </g>
              ))}
            </svg>
          </div>

          {/* Legend */}
          <div className="flex flex-wrap gap-3 mt-3">
            {Object.entries(AGENT_COLORS).map(([type, color]) => (
              <div key={type} className="flex items-center gap-1 text-xs text-gray-400">
                <span className="inline-block w-2 h-2 rounded-full" style={{ background: color }} />
                {type}
              </div>
            ))}
          </div>
        </div>

        {/* Agent Statuses */}
        <div className="bg-gray-900 border border-gray-700 rounded-lg p-4 overflow-auto max-h-80">
          <h2 className="text-sm font-semibold text-gray-300 mb-3">Agent Statuses</h2>
          <div className="space-y-2">
            {agents.length === 0 ? (
              <p className="text-gray-500 text-xs">Waiting for agents...</p>
            ) : (
              agents.map((agent) => (
                <div key={agent.name} className="flex items-center justify-between text-xs py-1 border-b border-gray-800">
                  <div className="flex items-center gap-2">
                    <span
                      className={`inline-block w-2 h-2 rounded-full ${AGENT_STATUS_DOT[agent.status] ?? "bg-gray-400"}`}
                    />
                    <span className="text-gray-200">{agent.name}</span>
                  </div>
                  <span className="text-gray-400">{agent.status}</span>
                </div>
              ))
            )}
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        {/* Incidents Table */}
        <div className="bg-gray-900 border border-gray-700 rounded-lg p-4">
          <h2 className="text-sm font-semibold text-gray-300 mb-3">
            Incidents ({incidents.length})
          </h2>
          <div className="overflow-auto max-h-64">
            <table className="w-full text-xs">
              <thead>
                <tr className="text-gray-500 border-b border-gray-700">
                  <th className="text-left py-1 pr-2">Type</th>
                  <th className="text-left py-1 pr-2">Sev</th>
                  <th className="text-left py-1 pr-2">Unit</th>
                  <th className="text-left py-1 pr-2">Status</th>
                  <th className="text-left py-1">Time</th>
                </tr>
              </thead>
              <tbody>
                {[...incidents].reverse().map((inc) => (
                  <tr key={inc.id} className="border-b border-gray-800 hover:bg-gray-800">
                    <td className="py-1 pr-2 text-gray-200">{inc.type}</td>
                    <td className="py-1 pr-2">
                      <span
                        className="inline-block w-4 h-4 rounded text-center leading-4 text-white text-xs font-bold"
                        style={{ background: inc.severity >= 7 ? "#ef4444" : inc.severity >= 4 ? "#f97316" : "#22c55e" }}
                      >
                        {inc.severity}
                      </span>
                    </td>
                    <td className="py-1 pr-2 text-gray-400">{inc.assignedUnit || "—"}</td>
                    <td className="py-1 pr-2">
                      <span className={`px-1 py-0.5 rounded text-xs ${STATUS_COLORS[inc.status] ?? ""}`}>
                        {inc.status}
                      </span>
                    </td>
                    <td className="py-1 text-gray-500">{inc.reportedAt?.slice(0, 8)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
            {incidents.length === 0 && (
              <p className="text-gray-500 text-xs py-4 text-center">No incidents yet...</p>
            )}
          </div>
        </div>

        {/* Event Log */}
        <div className="bg-gray-900 border border-gray-700 rounded-lg p-4">
          <h2 className="text-sm font-semibold text-gray-300 mb-3">Live Event Log</h2>
          <div className="overflow-auto max-h-64 space-y-1">
            {[...events].reverse().map((event, i) => (
              <div key={i} className="text-xs text-gray-400 font-mono leading-5">
                {event.includes("COMPLETED") ? (
                  <span className="text-green-400">{event}</span>
                ) : event.includes("ASSIGNED") ? (
                  <span className="text-blue-400">{event}</span>
                ) : event.includes("REPORTED") ? (
                  <span className="text-yellow-400">{event}</span>
                ) : event.includes("ABORTED") ? (
                  <span className="text-red-400">{event}</span>
                ) : (
                  event
                )}
              </div>
            ))}
            {events.length === 0 && (
              <p className="text-gray-500 text-xs py-4 text-center">Waiting for events...</p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
