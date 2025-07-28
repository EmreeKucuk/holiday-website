package com.emre.holidayapi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @GetMapping("/")
    @ResponseBody
    public String home() {
        return """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Holiday API - Interactive Explorer</title>
    <style>
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            margin: 0;
            padding: 20px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            color: #333;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
        }
        .header {
            text-align: center;
            color: white;
            margin-bottom: 40px;
        }
        .header h1 {
            font-size: 3rem;
            margin-bottom: 10px;
            text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
        }
        .header p {
            font-size: 1.2rem;
            opacity: 0.9;
            margin-bottom: 30px;
        }
        .frontend-link {
            display: inline-block;
            background: rgba(255,255,255,0.2);
            color: white;
            padding: 12px 24px;
            border-radius: 25px;
            text-decoration: none;
            font-weight: 500;
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255,255,255,0.3);
            transition: all 0.3s ease;
        }
        .frontend-link:hover {
            background: rgba(255,255,255,0.3);
            transform: translateY(-2px);
        }
        .endpoints-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
            gap: 30px;
            margin-top: 40px;
        }
        .endpoint-group {
            background: white;
            border-radius: 15px;
            padding: 25px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
            transition: transform 0.3s ease;
        }
        .endpoint-group:hover {
            transform: translateY(-5px);
        }
        .group-title {
            font-size: 1.5rem;
            font-weight: 600;
            margin-bottom: 20px;
            color: #4a5568;
            display: flex;
            align-items: center;
            gap: 10px;
        }
        .group-icon {
            width: 24px;
            height: 24px;
            background: #667eea;
            border-radius: 6px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 14px;
        }
        .endpoint {
            margin-bottom: 15px;
            padding: 15px;
            background: #f7fafc;
            border-radius: 8px;
            border-left: 4px solid #667eea;
        }
        .endpoint-header {
            display: flex;
            align-items: center;
            gap: 10px;
            margin-bottom: 8px;
        }
        .method {
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 12px;
            font-weight: 600;
            text-transform: uppercase;
        }
        .method.get { background: #c6f6d5; color: #22543d; }
        .method.post { background: #bee3f8; color: #2a4365; }
        .method.put { background: #faf089; color: #744210; }
        .method.delete { background: #fed7d7; color: #742a2a; }
        .endpoint-path {
            font-family: 'Monaco', 'Courier New', monospace;
            font-size: 14px;
            color: #4a5568;
            font-weight: 500;
        }
        .endpoint-desc {
            color: #718096;
            font-size: 14px;
            margin-top: 5px;
        }
        .test-button {
            background: #667eea;
            color: white;
            border: none;
            padding: 6px 12px;
            border-radius: 4px;
            font-size: 12px;
            cursor: pointer;
            margin-top: 8px;
            transition: background 0.2s;
        }
        .test-button:hover {
            background: #5a67d8;
        }
        .stats {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin: 30px 0;
        }
        .stat-card {
            background: rgba(255,255,255,0.15);
            backdrop-filter: blur(10px);
            border-radius: 12px;
            padding: 20px;
            text-align: center;
            color: white;
            border: 1px solid rgba(255,255,255,0.2);
        }
        .stat-number {
            font-size: 2rem;
            font-weight: 700;
            margin-bottom: 5px;
        }
        .stat-label {
            opacity: 0.9;
            font-size: 0.9rem;
        }
        .quick-test {
            background: rgba(255,255,255,0.1);
            backdrop-filter: blur(10px);
            border-radius: 12px;
            padding: 20px;
            margin: 30px 0;
            border: 1px solid rgba(255,255,255,0.2);
        }
        .quick-test h3 {
            color: white;
            margin-bottom: 15px;
        }
        .quick-links {
            display: flex;
            flex-wrap: wrap;
            gap: 10px;
        }
        .quick-link {
            background: rgba(255,255,255,0.2);
            color: white;
            padding: 8px 15px;
            border-radius: 20px;
            text-decoration: none;
            font-size: 14px;
            transition: all 0.2s;
            border: 1px solid rgba(255,255,255,0.3);
        }
        .quick-link:hover {
            background: rgba(255,255,255,0.3);
            transform: scale(1.05);
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>🎉 Holiday API</h1>
            <p>Interactive API Explorer & Documentation</p>
            <a href="http://localhost:5173" class="frontend-link">
                🚀 Open Full Frontend Application
            </a>
        </div>

        <div class="stats">
            <div class="stat-card">
                <div class="stat-number">25+</div>
                <div class="stat-label">API Endpoints</div>
            </div>
            <div class="stat-card">
                <div class="stat-number">5</div>
                <div class="stat-label">Endpoint Groups</div>
            </div>
            <div class="stat-card">
                <div class="stat-number">2</div>
                <div class="stat-label">Languages</div>
            </div>
            <div class="stat-card">
                <div class="stat-number">100%</div>
                <div class="stat-label">Test Coverage</div>
            </div>
        </div>

        <div class="quick-test">
            <h3>🔗 Quick Test Links</h3>
            <div class="quick-links">
                <a href="/api/countries" class="quick-link">Countries</a>
                <a href="/api/holidays/audiences" class="quick-link">Audiences</a>
                <a href="/api/holidays/types" class="quick-link">Holiday Types</a>
                <a href="/api/holidays/today?country=TR" class="quick-link">Today's Holidays</a>
                <a href="/api/holidays/range?start=2025-01-01&end=2025-01-31&country=TR" class="quick-link">January 2025</a>
                <a href="/api/holiday-templates" class="quick-link">Templates</a>
            </div>
        </div>

        <div class="endpoints-grid">
            <div class="endpoint-group">
                <h2 class="group-title">
                    <span class="group-icon">📅</span>
                    Holiday Management
                </h2>
                <div class="endpoint">
                    <div class="endpoint-header">
                        <span class="method get">GET</span>
                        <code class="endpoint-path">/api/holidays</code>
                    </div>
                    <div class="endpoint-desc">Get all holidays</div>
                    <button class="test-button" onclick="window.open('/api/holidays', '_blank')">Test</button>
                </div>
                <div class="endpoint">
                    <div class="endpoint-header">
                        <span class="method get">GET</span>
                        <code class="endpoint-path">/api/holidays/country/{code}</code>
                    </div>
                    <div class="endpoint-desc">Get holidays by country (supports language parameter)</div>
                    <button class="test-button" onclick="window.open('/api/holidays/country/TR?language=en', '_blank')">Test</button>
                </div>
                <div class="endpoint">
                    <div class="endpoint-header">
                        <span class="method get">GET</span>
                        <code class="endpoint-path">/api/holidays/today</code>
                    </div>
                    <div class="endpoint-desc">Check if today is a holiday</div>
                    <button class="test-button" onclick="window.open('/api/holidays/today?country=TR&language=en', '_blank')">Test</button>
                </div>
                <div class="endpoint">
                    <div class="endpoint-header">
                        <span class="method get">GET</span>
                        <code class="endpoint-path">/api/holidays/range</code>
                    </div>
                    <div class="endpoint-desc">Get holidays in date range</div>
                    <button class="test-button" onclick="window.open('/api/holidays/range?start=2025-01-01&end=2025-01-31&country=TR', '_blank')">Test</button>
                </div>
                <div class="endpoint">
                    <div class="endpoint-header">
                        <span class="method get">GET</span>
                        <code class="endpoint-path">/api/holidays/working-days</code>
                    </div>
                    <div class="endpoint-desc">Calculate working days between dates</div>
                    <button class="test-button" onclick="window.open('/api/holidays/working-days?start=2025-01-01&end=2025-01-31&country=TR', '_blank')">Test</button>
                </div>
                <div class="endpoint">
                    <div class="endpoint-header">
                        <span class="method get">GET</span>
                        <code class="endpoint-path">/api/holidays/types</code>
                    </div>
                    <div class="endpoint-desc">Get all holiday types</div>
                    <button class="test-button" onclick="window.open('/api/holidays/types', '_blank')">Test</button>
                </div>
                <div class="endpoint">
                    <div class="endpoint-header">
                        <span class="method post">POST</span>
                        <code class="endpoint-path">/api/holidays</code>
                    </div>
                    <div class="endpoint-desc">Create new holiday (requires JSON body)</div>
                </div>
                <div class="endpoint">
                    <div class="endpoint-header">
                        <span class="method put">PUT</span>
                        <code class="endpoint-path">/api/holidays/{id}</code>
                    </div>
                    <div class="endpoint-desc">Update existing holiday</div>
                </div>
                <div class="endpoint">
                    <div class="endpoint-header">
                        <span class="method delete">DELETE</span>
                        <code class="endpoint-path">/api/holidays/{id}</code>
                    </div>
                    <div class="endpoint-desc">Delete holiday by ID</div>
                </div>
            </div>

            <div class="endpoint-group">
                <h2 class="group-title">
                    <span class="group-icon">👥</span>
                    Audience Management
                </h2>
                <div class="endpoint">
                    <div class="endpoint-header">
                        <span class="method get">GET</span>
                        <code class="endpoint-path">/api/holidays/audiences</code>
                    </div>
                    <div class="endpoint-desc">Get all audiences</div>
                    <button class="test-button" onclick="window.open('/api/holidays/audiences', '_blank')">Test</button>
                </div>
                <div class="endpoint">
                    <div class="endpoint-header">
                        <span class="method get">GET</span>
                        <code class="endpoint-path">/api/holidays/audiences/translated</code>
                    </div>
                    <div class="endpoint-desc">Get translated audiences</div>
                    <button class="test-button" onclick="window.open('/api/holidays/audiences/translated?language=en', '_blank')">Test</button>
                </div>
                <div class="endpoint">
                    <div class="endpoint-header">
                        <span class="method post">POST</span>
                        <code class="endpoint-path">/api/holidays/audiences</code>
                    </div>
                    <div class="endpoint-desc">Create new audience</div>
                </div>
                <div class="endpoint">
                    <div class="endpoint-header">
                        <span class="method put">PUT</span>
                        <code class="endpoint-path">/api/holidays/audiences/{id}</code>
                    </div>
                    <div class="endpoint-desc">Update audience</div>
                </div>
                <div class="endpoint">
                    <div class="endpoint-header">
                        <span class="method delete">DELETE</span>
                        <code class="endpoint-path">/api/holidays/audiences/{id}</code>
                    </div>
                    <div class="endpoint-desc">Delete audience</div>
                </div>
            </div>

            <div class="endpoint-group">
                <h2 class="group-title">
                    <span class="group-icon">🌍</span>
                    Countries & Geography
                </h2>
                <div class="endpoint">
                    <div class="endpoint-header">
                        <span class="method get">GET</span>
                        <code class="endpoint-path">/api/countries</code>
                    </div>
                    <div class="endpoint-desc">Get all supported countries</div>
                    <button class="test-button" onclick="window.open('/api/countries', '_blank')">Test</button>
                </div>
            </div>

            <div class="endpoint-group">
                <h2 class="group-title">
                    <span class="group-icon">🤖</span>
                    AI Chat & Intelligence
                </h2>
                <div class="endpoint">
                    <div class="endpoint-header">
                        <span class="method post">POST</span>
                        <code class="endpoint-path">/api/chat</code>
                    </div>
                    <div class="endpoint-desc">Chat with AI about holidays (requires JSON body with message)</div>
                </div>
            </div>

            <div class="endpoint-group">
                <h2 class="group-title">
                    <span class="group-icon">📋</span>
                    Templates & Data Structures
                </h2>
                <div class="endpoint">
                    <div class="endpoint-header">
                        <span class="method get">GET</span>
                        <code class="endpoint-path">/api/holiday-templates</code>
                    </div>
                    <div class="endpoint-desc">Get all holiday templates</div>
                    <button class="test-button" onclick="window.open('/api/holiday-templates', '_blank')">Test</button>
                </div>
            </div>
        </div>

        <div style="margin-top: 50px; text-align: center; color: white; opacity: 0.8;">
            <p>🚀 <strong>Holiday API v1.0</strong> - Built with Spring Boot & AI Integration</p>
            <p>For full interactive frontend with forms and advanced features, visit: 
                <a href="http://localhost:5173" style="color: #ffd700;">http://localhost:5173</a>
            </p>
        </div>
    </div>

    <script>
        // Add some interactivity
        document.addEventListener('DOMContentLoaded', function() {
            // Animate stats on load
            const statNumbers = document.querySelectorAll('.stat-number');
            statNumbers.forEach((stat, index) => {
                setTimeout(() => {
                    stat.style.transform = 'scale(1.1)';
                    setTimeout(() => {
                        stat.style.transform = 'scale(1)';
                    }, 200);
                }, index * 100);
            });
        });
    </script>
</body>
</html>
                """;
    }
}
