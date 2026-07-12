#version 120

uniform float iTime;
uniform float u_progress;

void main() {
    vec2 uv = gl_TexCoord[0].st;

    float horiz = 1.0 - abs(uv.x - 0.5) * 2.0;
    horiz = smoothstep(0.0, 1.0, horiz);

    float vert = 1.0 - uv.y;
    vert = pow(vert, 0.4);

    float flow = sin(uv.y * 30.0 - iTime * 8.0) * 0.15 + 0.85;
    float pulse = sin(uv.y * 10.0 - iTime * 3.0) * 0.1 + 0.9;

    vec3 core = vec3(0.7, 1.0, 0.5);
    vec3 outer = vec3(0.1, 0.6, 0.2);
    vec3 col = mix(outer, core, horiz) * flow * pulse;

    float alpha = horiz * vert;

    float fade = 1.0;
    if (u_progress < 0.1) {
        fade = u_progress / 0.1;
    } else if (u_progress > 0.9) {
        fade = (1.0 - u_progress) / 0.1;
    }
    alpha *= fade;

    gl_FragColor = vec4(col, alpha);
}